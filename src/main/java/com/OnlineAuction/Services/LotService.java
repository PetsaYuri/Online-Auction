package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.*;
import com.OnlineAuction.Repositories.LotsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LotService {

    private final LotsRepository lotsRepository;

    private final UserService userService;

    private final AuctionService auctionService;

    private final HistoryOfPriceService historyOfPriceService;

    private final CategoryService categoryService;

    @Autowired
    public LotService(LotsRepository lotsRepository, UserService userService, @Lazy AuctionService auctionService,
                      @Lazy HistoryOfPriceService historyOfPriceService, @Lazy CategoryService categoryService) {
        this.lotsRepository = lotsRepository;
        this.userService = userService;
        this.auctionService = auctionService;
        this.historyOfPriceService = historyOfPriceService;
        this.categoryService = categoryService;
    }

    public Long generateId() {
        int count = 0;
        while (count < 100) {
            String uuid = String.format("%06d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
            Long id = Long.valueOf(uuid.substring(0, 10));
            Optional<Lot> lot = lotsRepository.findById(id);
            if (lot.isEmpty()) {
                return id;
            }
            count++;
        }
        throw new UnableToGenerateIdException();
    }

    public List<Lot> getLots() {
        return lotsRepository.findAll();
    }

    public List<Lot> getLotsWithoutAuction() {
        List<Lot> lots = new ArrayList<>();
        for (Lot lot : lotsRepository.findAll()) {
            if (lot.getAuction() == null) {
                lots.add(lot);
            }
        }
        return lots;
    }

    public Lot getLotById(Long id) {
        return lotsRepository.getReferenceById(id);
    }

    //Will be added soon
    //public Lot getLotByNameAndAuction() {}

    public Lot create(LotDTO lotDTO) {
        User creator = userService.getUserByEmail("test");

        if (creator == null) {
            throw new EntityNotFoundException();
        }

        Long id = generateId();
        Lot newLot = new Lot(id, lotDTO, creator);
        Lot savedLot = lotsRepository.save(newLot);
        categoryService.addLotToCategory(savedLot);

        if (getLotsWithoutAuction().size() >= auctionService.getQuantity()) {
            auctionService.autoCreate();
        }

        return savedLot;
    }

    public Lot update(LotDTO lotDTO, Long id_lot) {
        Lot existLot = lotsRepository.getReferenceById(id_lot);

        if (lotDTO.name() != null) {
            existLot.setName(lotDTO.name());
        }

        if (lotDTO.description() != null) {
            existLot.setDescription(lotDTO.description());
        }

        if (lotDTO.image() != null) {
            existLot.setImage(lotDTO.image());
        }

        if (lotDTO.minimum_price() != 0) {
            existLot.setMinimum_price(lotDTO.minimum_price());
        }

        if (lotDTO.category() != null && lotDTO.category() != existLot.getCategory()) {
            Category category = categoryService.getOne(lotDTO.category().getId());
            categoryService.unsetLotFromCategory(existLot);
            existLot.setCategory(category);
            categoryService.addLotToCategory(existLot);
        }

        return lotsRepository.save(existLot);
    }

    public boolean delete(Long id) {
        if (!lotsRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find Lot with id " + id);
        }

        Lot lot = lotsRepository.getReferenceById(id);
        Auction auction = lot.getAuction();

        if (auction != null) {
            auction = auctionService.removeLotFromAuction(lot);
        }

        if (lot.getCategory() != null) {
            categoryService.unsetLotFromCategory(lot);
        }

        if (lot.getHistoryOfPrice() != null) {
            historyOfPriceService.deleteLotFromHistoryOfPrice(lot);
        }

        lotsRepository.delete(lot);

        if (auction != null) {
            if (auction.getLots().isEmpty()) {
                auctionService.delete(auction.getId());
            }
        }

        return true;
    }

    public void setAuction(List<Lot> lots, Auction auction) {
        List<Lot> updatedList = lots.stream().peek(lot -> lot.setAuction(auction)).toList();
        lotsRepository.saveAll(updatedList);
    }

    public Auction unsetAuction(Lot lot) {
        Auction auction = lot.getAuction();
        lot.setAuction(null);
        lotsRepository.save(lot);
        return auction;
    }

    public void setCurrentPrice(Lot lot, int current_price) {
        if (current_price > lot.getCurrent_price()) {
            lot.setCurrent_price(current_price);
            lotsRepository.save(lot);
        }   else {
            throw new RuntimeException();
        }
    }

    public void setHistoryOfPrice(Lot lot, HistoryOfPrice historyOfPrice) {
        lot.setHistoryOfPrice(historyOfPrice);
        lotsRepository.save(lot);
    }

    public void unsetHistoryOfPrice(Lot lot) {
        lot.setHistoryOfPrice(null);
        lotsRepository.save(lot);
    }

    public HistoryOfPrice makeBet(HistoryOfPriceDTO historyOfPriceDTO, Long idLot) {
        return historyOfPriceService.add(historyOfPriceDTO, idLot);
    }
}