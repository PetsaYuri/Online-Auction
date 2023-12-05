package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Exceptions.UnableToGenerateIdException;
import com.OnlineAuction.Models.*;
import com.OnlineAuction.Repositories.LotsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
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

    private final BetService betService;

    private final CategoryService categoryService;

    @Autowired
    public LotService(LotsRepository lotsRepository, @Lazy UserService userService, @Lazy AuctionService auctionService,
                      @Lazy BetService betService, @Lazy CategoryService categoryService) {
        this.lotsRepository = lotsRepository;
        this.userService = userService;
        this.auctionService = auctionService;
        this.betService = betService;
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

    public List<Lot> getAll(Pageable pageable) {
        return lotsRepository.findAll(pageable).toList();
    }

    public List<Lot> getAll() {
        return lotsRepository.findAll();
    }

    public List<Lot> getByName(String name, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCase(name, pageable).toList();
    }

    public List<Lot> getByCurrentPriceInRange(int priceGreaterThan, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByCurrentPriceBetween(priceGreaterThan, priceLessThan, pageable).toList();
    }

    public List<Lot> getByCurrentPriceGreaterThan(int price, Pageable pageable) {
        return lotsRepository.findByCurrentPriceGreaterThan(price, pageable).toList();
    }

    public List<Lot> getByCurrentPriceLessThan(int price, Pageable pageable) {
        return lotsRepository.findByCurrentPriceLessThan(price, pageable).toList();
    }

    public List<Lot> getByAvailable(boolean isAvailable, Pageable pageable) {
        return lotsRepository.findByIsAvailable(isAvailable, pageable).toList();
    }

    public List<Lot> getByNameAndAvailableAndCurrentPriceInRange(String name, boolean isAvailable, int priceGreaterThan, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceBetween(name, isAvailable, priceGreaterThan, priceLessThan, pageable).toList();
    }

    public List<Lot> getByNameAndAvailableAndCurrentPriceGreaterThan(String name, boolean isAvailable, int priceGreaterThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceGreaterThan(name, isAvailable, priceGreaterThan, pageable).toList();
    }

    public List<Lot> getByNameAndAvailableAndCurrentPriceLessThan(String name, boolean isAvailable, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceLessThan(name, isAvailable, priceLessThan, pageable).toList();
    }

    public List<Lot> getByNameAndCurrentPriceInRange(String name, int priceGreaterThan, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndCurrentPriceBetween(name, priceGreaterThan, priceLessThan, pageable).toList();
    }

    public List<Lot> getByNameAndCurrentPriceGreaterThan(String name, int priceGreaterThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndCurrentPriceGreaterThan(name, priceGreaterThan, pageable).toList();
    }

    public List<Lot> getByNameAndCurrentPriceLessThan(String name, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByNameContainsIgnoreCaseAndCurrentPriceLessThan(name, priceLessThan, pageable).toList();
    }

    public List<Lot> getByAvailableAndCurrentPriceInRange(boolean isAvailable, int priceGreaterThan, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByIsAvailableAndCurrentPriceBetween(isAvailable, priceGreaterThan, priceLessThan, pageable).toList();
    }

    public List<Lot> getByAvailableAndCurrentPriceGreaterThan( boolean isAvailable, int priceGreaterThan, Pageable pageable) {
        return lotsRepository.findByIsAvailableAndCurrentPriceGreaterThan(isAvailable, priceGreaterThan, pageable).toList();
    }

    public List<Lot> getByAvailableAndCurrentPriceLessThan(boolean isAvailable, int priceLessThan, Pageable pageable) {
        return lotsRepository.findByIsAvailableAndCurrentPriceLessThan(isAvailable, priceLessThan, pageable).toList();
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
            existLot.setMinimumPrice(lotDTO.minimum_price());
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
        Lot lot = lotsRepository.getReferenceById(id);
        Auction auction = lot.getAuction();

        if (auction != null) {
            auction = auctionService.removeLotFromAuction(lot);
        }

        if (!lot.getBets().isEmpty()) {
            List<Bet> list = lot.getBets();
            for (Bet bet : list) {
                betService.delete(bet.getId());
            }
        }

        if (lot.getWinner() != null) {
            userService.removeLotFromListLotsOfWinning(lot);
        }

        categoryService.unsetLotFromCategory(lot);
        userService.removeLotFromListOfCreatedLots(lot);
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
        if (current_price > lot.getCurrentPrice()) {
            lot.setCurrentPrice(current_price);
            lotsRepository.save(lot);
        }   else {
            throw new RuntimeException();
        }
    }

    public void addBet(Lot lot, Bet bet) {
        List<Bet> bets = lot.getBets();
        bets.add(bet);
        lot.setBets(bets);
        lotsRepository.save(lot);
    }

    public void removeBet(Lot lot) {
        lot.setBets(null);
        lotsRepository.save(lot);
    }

    public Bet makeBet(BetDTO betDTO, Long idLot) {
        return betService.add(betDTO, idLot);
    }

    public void setWinner(Lot lot, User winner) {
        lot.setWinner(winner);
        lotsRepository.save(lot);
    }

    public void removeWinnerFromLot(Lot lot) {
        lot.setWinner(null);
        lotsRepository.save(lot);
    }

    public void removeAuctionFromLotList(List<Lot> lots) {
        for(Lot lot : lots) {
            lot.setAuction(null);
            lotsRepository.save(lot);
        }
    }

    public void setAvailable(List<Lot> lots, boolean value) {
        for (Lot lot : lots) {
            lot.setAvailable(value);
        }
        lotsRepository.saveAll(lots);
    }

    public void setAvailable(Lot lot, boolean value) {
        lot.setAvailable(value);
        lotsRepository.save(lot);
    }
}