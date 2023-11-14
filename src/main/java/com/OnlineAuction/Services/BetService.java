package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.BetsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BetService {

    private final BetsRepository betsRepository;

    private final UserService userService;

    private final LotService lotService;

    @Autowired
    public BetService(BetsRepository betsRepository, UserService userService, LotService lotService) {
        this.betsRepository = betsRepository;
        this.userService = userService;
        this.lotService = lotService;
    }

    public Bet getById(Long id) {
        return betsRepository.getReferenceById(id);
    }

    public List<Bet> getAll() {
        return betsRepository.findAll();
    }

    public Bet add(BetDTO betDTO, Long id) {
        User user = userService.getOne(1L);
        Lot lot = lotService.getLotById(id);

        if (betDTO.price() > lot.getCurrent_price()) {
            Bet newBet = new Bet(betDTO, user, lot);
            betsRepository.save(newBet);
            lotService.setCurrentPrice(newBet.getLot(), newBet.getPrice());
            lotService.addBet(newBet.getLot(), newBet);
            userService.addBet(user, newBet);
            return newBet;
        }
        throw new UnableToCreateException("Unable to create bet. Price is lower than current price");
    }

    public Bet update(BetDTO betDTO, Long id) {
        Bet existBet = betsRepository.getReferenceById(id);

        if (betDTO.price() != 0 && betDTO.price() != existBet.getPrice() && betDTO.price() > existBet.getLot().getCurrent_price()) {
            existBet.setPrice(betDTO.price());
            lotService.setCurrentPrice(existBet.getLot(), betDTO.price());
        }

        return betsRepository.save(existBet);
    }

    public boolean delete(Long id) {
        if (!betsRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find bet with id " + id);
        }

        Bet bet = betsRepository.getReferenceById(id);
        lotService.removeBet(bet.getLot());
        userService.deleteBet(bet.getUser());
        betsRepository.delete(bet);
        return true;
    }

    public void deleteLotFromBetsList(Lot lot) {
        List<Bet> bets = lot.getBets();
        bets.stream().peek(bet -> {
            bet.setLot(null);
            betsRepository.save(bet);
        });
    }

    public List<Bet> getBetsByLot(Lot lot) {
        return betsRepository.findByLot(lot);
    }
}