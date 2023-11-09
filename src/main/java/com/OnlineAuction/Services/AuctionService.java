package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.AuctionDTO;
import com.OnlineAuction.DTO.AuctionPropertiesDTO;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Repositories.AuctionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuctionService {

    private final AuctionsRepository auctionsRepository;

    private final LotService lotService;

    @Value("${auction.quantity}")
    private int quantity;

    @Value("${auction.duration}")
    private int duration;

    @Autowired
    public AuctionService(AuctionsRepository auctionsRepository, LotService lotService) {
        this.auctionsRepository = auctionsRepository;
        this.lotService = lotService;
    }

    public List<Auction> getAll() {
        return auctionsRepository.findAll();
    }

    public Auction getOneById(Long id) {
        return auctionsRepository.getReferenceById(id);
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
        }

        Auction newAuction = new Auction(id, auctionDTO);
        return auctionsRepository.save(newAuction);
    }

    public Auction autoCreate() {
        if (lotService.getLots().size() >= quantity) {
            Long id = generateId();
            List<Lot> lots = lotService.getLotsWithoutAuction().subList(0, quantity);
            String description = fillingDescription(lots);
            Auction newAuction = new Auction(id, description, lots, duration);
            auctionsRepository.save(newAuction);
            lotService.setAuction(lots, newAuction);
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
            }

            List<Lot> receivedLots = new ArrayList<>();

            for (Lot lot : auctionDTO.lots()) {
                Lot recLot = lotService.getLotById(lot.getId());
                if (auctionDTO.lots().contains(recLot)) {
                    continue;
                }

                lotService.unsetAuction(recLot);
                receivedLots.add(recLot);
            }

            existAuction.setLots(receivedLots);
            lotService.setAuction(receivedLots, existAuction);
        }

        return auctionsRepository.save(existAuction);
    }

    public boolean delete(Long id) {
        if (!auctionsRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find Auction with id " + id);
        }

        Auction auction = auctionsRepository.getReferenceById(id);
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
}