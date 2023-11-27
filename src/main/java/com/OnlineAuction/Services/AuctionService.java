package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.AuctionDTO;
import com.OnlineAuction.DTO.AuctionPropertiesDTO;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.*;
import com.OnlineAuction.Repositories.AuctionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuctionService {

    private final AuctionsRepository auctionsRepository;

    private final LotService lotService;

    private final UserService userService;

    private final ResultOfAuctionService resultOfAuctionService;

    private final BetService betService;

    @Value("${auction.quantity}")
    private int quantity;

    @Value("${auction.duration}")
    private int duration;

    @Autowired
    public AuctionService(AuctionsRepository auctionsRepository, LotService lotService, UserService userService,
                          ResultOfAuctionService resultOfAuctionService, BetService betService) {
        this.auctionsRepository = auctionsRepository;
        this.lotService = lotService;
        this.userService = userService;
        this.resultOfAuctionService = resultOfAuctionService;
        this.betService = betService;
        checkAuctions();
    }

    public List<Auction> getAll() {
        return auctionsRepository.findAll();
    }

    @Transactional
    public Auction getOneById(Long id) {
        return auctionsRepository.getReferenceById(id);
    }

    public List<Auction> getAuctionListWithoutResult() {
        return auctionsRepository.findByResultOfAuction(null);
    }

    public Long generateId() {
        int count = 0;
        while (count < 100) {
            String uuid = String.format("%06d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
            Long id = Long.valueOf(uuid.substring(0, 10));
            Optional<Auction> auction = auctionsRepository.findById(id);
            if (auction.isEmpty()) {
                return id;
            }
            count++;
        }
        throw new UnableToGenerateIdException();
    }

    public Auction create(AuctionDTO auctionDTO) {
        Long id = generateId();

        if (!auctionDTO.lots().isEmpty()) {
            for (Lot lot : auctionDTO.lots()) {
                lot = lotService.getLotById(lot.getId());

                if (lot.getAuction() != null) {
                    List<Lot> updatedLots = lot.getAuction().getLots();
                    updatedLots.remove(lot);
                    lot.getAuction().setLots(updatedLots);
                    auctionsRepository.save(lot.getAuction());
                    Auction auction = lotService.unsetAuction(lot);

                    if (auction.getLots().isEmpty()) {
                        auctionsRepository.delete(auction);
                    }
                }
            }
        }   else {
            throw new UnableToCreateException("Unable to create the auction. Make sure your body has the lots.");
        }

        Auction newAuction = new Auction(id, auctionDTO);
        auctionsRepository.save(newAuction);
        lotService.setAuction(auctionDTO.lots(), newAuction);
        lotService.setAvailable(auctionDTO.lots(), true);
        setTimer(newAuction);
        return newAuction;
    }

    public Auction autoCreate() {
        if (lotService.getLots().size() >= quantity) {
            Long id = generateId();
            List<Lot> lots = lotService.getLotsWithoutAuction().subList(0, quantity);
            String description = fillingDescription(lots);
            Auction newAuction = new Auction(id, description, lots, duration);
            auctionsRepository.save(newAuction);
            lotService.setAuction(lots, newAuction);
            lotService.setAvailable(lots, true);
            setTimer(newAuction);
            return newAuction;
        }
        throw new UnableToCreateException("Unable to create auction. Lots are less than required");
    }

    public Auction update(Long idAuction, AuctionDTO auctionDTO) {
        Auction existAuction = auctionsRepository.getReferenceById(idAuction);

        if (auctionDTO.title() != null && !existAuction.getTitle().equals(auctionDTO.title())) {
            existAuction.setTitle(auctionDTO.title());
        }

        if (auctionDTO.description() != null && !existAuction.getDescription().equals(auctionDTO.description())) {
            existAuction.setDescription(auctionDTO.description());
        }

        if (auctionDTO.lots() != null) {
            for (Lot lot : existAuction.getLots()) {
                if (auctionDTO.lots().contains(lot)) {
                    continue;
                }

                lotService.unsetAuction(lot);
                lotService.setAvailable(lot, false);
            }

            List<Lot> receivedLots = new ArrayList<>();

            for (Lot lot : auctionDTO.lots()) {
                Lot recLot = lotService.getLotById(lot.getId());
                if (existAuction.getLots().contains(recLot)) {
                    continue;
                }

                if (recLot.getAuction() != null) {
                    lotService.unsetAuction(recLot);
                    lotService.setAvailable(recLot, false);
                }

                receivedLots.add(recLot);
            }

            existAuction.setLots(receivedLots);
            lotService.setAuction(receivedLots, existAuction);
            lotService.setAvailable(receivedLots, true);
        }

        return auctionsRepository.save(existAuction);
    }

    public boolean delete(Long id) {
        if (!auctionsRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find Auction with id " + id);
        }

        Auction auction = auctionsRepository.getReferenceById(id);

        if (!auction.getLots().isEmpty()) {
            lotService.removeAuctionFromLotList(auction.getLots());
            lotService.setAvailable(auction.getLots(), false);
        }

        if (auction.getResultOfAuction() != null) {
            resultOfAuctionService.removeResultFromAuction(auction.getResultOfAuction());
        }

        auctionsRepository.delete(auction);
        return true;
    }

    public Auction removeLotFromAuction(Lot lot) {
        Auction auction = lot.getAuction();
        List<Lot> lots = auction.getLots();
        lots.remove(lot);
        auction.setLots(lots);
        return auctionsRepository.save(auction);
    }

    public String fillingDescription(List<Lot> lots) {
        String description = "Items: ";
        for (Lot lot : lots) {
            description += lot.getName() + ", ";
        }
        StringBuilder stringBuilder = new StringBuilder(description);
        return stringBuilder.substring(0, description.length() - 1);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity == 0) {
            throw new NullPointerException("Quantity value is 0");
        }

        boolean result = changeApplicationProperties("auction.quantity", String.valueOf(quantity));
        if (result) {
            this.quantity = quantity;
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (duration == 0) {
            throw new NullPointerException("Duration value is 0");
        }

        boolean result = changeApplicationProperties("auction.duration", String.valueOf(duration));
        if (result) {
            this.duration = duration;
        }
    }

    public boolean changeApplicationProperties(String name, String value) {
        try {
            File file = new File( "src/main/resources/application.properties");
            String[] content = getContentFromFile(file).split(name, 2);
            String arr[] = content[1].split("\n", 2);
            String fileEnd = "";
            if (arr.length > 1)    {
                fileEnd += arr[1];
            }
            fileEnd = fileEnd.isEmpty() ? "" : "\n" + fileEnd;
            if (file.exists())  {
                FileWriter fw = new FileWriter(file);
                fw.write(content[0] + name + "=" + value + fileEnd);
                fw.flush();
                fw.close();
                return true;
            }
        }   catch (IOException ex)  {
            System.out.println(ex.getStackTrace());
        }
        return false;
    }

    public String getContentFromFile(File file)   {
        String content = "";
        try {
            FileReader fileReader = new FileReader(file);
            int c;
            while ((c= fileReader.read()) != -1)  {
                content += (char) c;
            }
            fileReader.close();
        }   catch (FileNotFoundException ex)    {
            System.out.println("File \"" + file.getName() + "\" not found");
        }   catch (IOException ex)  {
            System.out.println("IOException");
        }
        return content;
    }

    public AuctionPropertiesDTO updateAuctionProperties(AuctionPropertiesDTO auctionPropertiesDTO) {
        if (auctionPropertiesDTO.quantity() != getQuantity()) {
            setQuantity(auctionPropertiesDTO.quantity());
        }

        if (auctionPropertiesDTO.duration() != getDuration()) {
            setDuration(auctionPropertiesDTO.duration());
        }
        return new AuctionPropertiesDTO(getQuantity(), getDuration());
    }

    public void setTimer(Auction auction) {
        Instant currentInstant = Instant.ofEpochMilli(System.currentTimeMillis());
        long time = ChronoUnit.MILLIS.between(currentInstant, auction.getEnds().toInstant());
     //   long time = 60000 / 2;
        TimerTask timerTask = new CreatingResultOfAuction(auction);
        Timer timer = new Timer();
        timer.schedule(timerTask, time);
    }

    public class CreatingResultOfAuction extends TimerTask {

        private final Auction auction;

        public CreatingResultOfAuction(Auction auction) {
            this.auction = auction;
        }

        @Override
        public void run() {
            performResultsOfAuction(auction);
        }
    }

    public void performResultsOfAuction(Auction auction) {
        List<Lot> lots = auction.getLots();
        for (Lot lot : lots) {
            List<Bet> bets = betService.getBetsByLot(lot);
            if (bets.isEmpty()) {
                continue;
            }

            Bet lastBet = bets.get(bets.size() - 1);
            User winner = lastBet.getUser();
            lotService.setWinner(lot, winner);
            userService.addLotToListLotsOfWinning(winner, lot);
            lotService.setAvailable(lot, false);
        }

        resultOfAuctionService.create(auction.getId());
    }

    public void checkAuctions() {
        if (!auctionsRepository.findAll().isEmpty()) {
            List<Auction> auctions = getAuctionListWithoutResult();
            for (Auction auction : auctions) {
                if (new Date().toInstant().isBefore(auction.getEnds().toInstant())) {
                    setTimer(auction);
                }   else {
                    performResultsOfAuction(auction);
                }
            }
        }
    }
}