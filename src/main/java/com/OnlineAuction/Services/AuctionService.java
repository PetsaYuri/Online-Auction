package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.AuctionDTO;
import com.OnlineAuction.DTO.AuctionPropertiesDTO;
import com.OnlineAuction.Exceptions.Auction.AuctionHasBeenDeletedException;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.*;
import com.OnlineAuction.Repositories.AuctionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
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

    public List<Auction> getByActive(Pageable pageable, boolean active) {
        if (active) {
            return auctionsRepository.findByResultOfAuction(null, pageable).toList();
        }
        return auctionsRepository.findAll(pageable).toList();
    }

    public List<Auction> getByTitleAndActiveAndStartAndEnd(String title, boolean active, Timestamp start, Timestamp end, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByTitleContainsIgnoreCaseAndResultOfAuctionAndStartAfterAndEndsBefore(title, null, start, end, pageable).toList();
        }
        return auctionsRepository.findByTitleContainsIgnoreCaseAndStartAfterAndEndsBefore(title, start, end, pageable).toList();
    }

    public List<Auction> getByTitleAndActiveAndStart(String title, boolean active, Timestamp start, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByTitleContainsIgnoreCaseAndResultOfAuctionAndStartAfter(title, null, start, pageable).toList();
        }
        return auctionsRepository.findByTitleContainsIgnoreCaseAndStartAfter(title, start, pageable).toList();
    }

    public List<Auction> getByTitleAndActiveAndEnd(String title, boolean active, Timestamp end, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByTitleContainsIgnoreCaseAndResultOfAuctionAndEndsBefore(title, null, end, pageable).toList();
        }
        return auctionsRepository.findByTitleContainsIgnoreCaseAndEndsBefore(title, end, pageable).toList();
    }

    public List<Auction> getByTitleAndActive(String title, boolean active, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByTitleContainsIgnoreCaseAndResultOfAuction(title, null, pageable).toList();
        }
        return auctionsRepository.findByTitleContainsIgnoreCase(title, pageable).toList();
    }

    public List<Auction> getByStartAndEndAndActive(Timestamp start, Timestamp end, boolean active, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByStartAfterAndEndsBeforeAndResultOfAuction(start, end, null, pageable).toList();
        }
        return auctionsRepository.findByStartAfterAndEndsBefore(start, end, pageable).toList();
    }

    public List<Auction> getAfterStartAndActive(Timestamp start, boolean active, Pageable pageable) {
        if (active) {
            return auctionsRepository.findByStartAfterAndResultOfAuction(start, null, pageable).toList();
        }
        return auctionsRepository.findByStartAfter(start, pageable).toList();
    }

    public List<Auction> getBeforeEndAndActive(Timestamp end, boolean active, Pageable pageable) {
        if (active) {
            return  auctionsRepository.findByEndsBeforeAndResultOfAuction(end, null, pageable).toList();
        }
        return auctionsRepository.findByEndsBefore(end, pageable).toList();
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
        if (lotService.getAll().size() >= quantity) {
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
            List<Lot> receivedLots = new ArrayList<>(auctionDTO.lots().stream().map(lot -> lotService.getLotById(lot.getId())).toList());

            for (Lot lot : existAuction.getLots()) {
                if (receivedLots.contains(lot)) {
                    continue;
                }

                lotService.unsetAuction(lot);
                lotService.setAvailable(lot, false);
            }

            List<Lot> updatedLotsList = new ArrayList<>();

            for (Lot lot : receivedLots) {
                if (existAuction.getLots().contains(lot)) {
                    updatedLotsList.add(lot);
                    continue;
                }

                if (lot.getAuction() != null) {
                    Auction removedAuction = lotService.unsetAuction(lot);
                    lotService.setAvailable(lot, false);
                    removeLotFromAuction(lot, removedAuction);
                }

                updatedLotsList.add(lot);
            }

            existAuction.setLots(updatedLotsList);
            lotService.setAuction(updatedLotsList, existAuction);
            lotService.setAvailable(updatedLotsList, true);
            existAuction.setDescription(fillingDescription(existAuction.getLots()));
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

    public void removeLotFromAuction(Lot lot, Auction auction) {
        List<Lot> lots = auction.getLots();
        lots.remove(lot);
        auction.setLots(lots);
        auctionsRepository.save(auction);

        if (lots.isEmpty()) {
            delete(auction.getId());
        }
    }

    public String fillingDescription(List<Lot> lots) {
        String description = "Items: ";
        for (Lot lot : lots) {
            description += lot.getName() + ", ";
        }
        StringBuilder stringBuilder = new StringBuilder(description);
        return stringBuilder.substring(0, description.length() - 2);
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
      //  long time = 60000 / 2;
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

    public Auction performResultsOfAuction(Auction auction) {
        List<Lot> lots = auction.getLots();
        int size = lots.size();
        for (int i = 0; i < size; i++) {
            if (lots.isEmpty()) {
                break;
            }

            List<Bet> bets = betService.getBetsByLot(lots.get(i));
            if (bets.isEmpty()) {
                lotService.unsetAuction(lots.get(i));
                removeLotFromAuction(lots.get(i), auction);
                i--;
            }   else {
                Bet lastBet = bets.get(bets.size() - 1);
                User winner = lastBet.getUser();
                lotService.setWinner(lots.get(i), winner);
                userService.addLotToListLotsOfWinning(winner, lots.get(i));
                lotService.setAvailable(lots.get(i), false);
            }
        }

        if (!lots.isEmpty()) {
            resultOfAuctionService.create(auction.getId());
            return getOneById(auction.getId());
        }
        throw new AuctionHasBeenDeletedException("The auction has been deleted, because it's empty. The auction result cannot be created.");
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