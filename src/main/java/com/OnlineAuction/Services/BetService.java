package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Exceptions.UnableToDeleteException;
import com.OnlineAuction.Exceptions.User.UserDoesNotHaveAccessException;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.BetsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class BetService {

    private final BetsRepository betsRepository;

    private final UserService userService;

    private final LotService lotService;

    @Autowired
    public BetService(BetsRepository betsRepository, @Lazy UserService userService, LotService lotService) {
        this.betsRepository = betsRepository;
        this.userService = userService;
        this.lotService = lotService;
    }

    public Bet getById(Long id) {
        return betsRepository.getReferenceById(id);
    }

    public List<Bet> getAll(Pageable pageable) {
        return betsRepository.findAll(pageable).toList();
    }

    public List<Bet> getAllInBetweenDays(Timestamp start, Timestamp end, Pageable pageable) {
        return betsRepository.findByDateBetween(start, end, pageable).toList();
    }

    public List<Bet> getAllBeforeDate(Timestamp date, Pageable pageable) {
        return betsRepository.findByDateBefore(date, pageable).toList();
    }

    public List<Bet> getAllAfterDate(Timestamp date, Pageable pageable) {
        return betsRepository.findByDateAfter(date, pageable).toList();
    }

    public Bet add(BetDTO betDTO, Long id) {
        User user = userService.getOne(1L);
        Lot lot = lotService.getLotById(id);
        User sender = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (lot.getCreator().equals(sender) || sender.getRole().equals("owner") || sender.getRole().equals("admin")) {
            if (!lot.isAvailable()) {
                throw new UnableToCreateException("Unable to add a bet. The auction has probably ended.");
            }

            if (betDTO.price() > lot.getCurrentPrice()) {
                Bet newBet = new Bet(betDTO, user, lot);
                betsRepository.save(newBet);
                lotService.setCurrentPrice(newBet.getLot(), newBet.getPrice());
                lotService.addBet(newBet.getLot(), newBet);
                userService.setBetToUser(user, newBet);
                return newBet;
            }
            throw new UnableToCreateException("Unable to create bet. Price is lower than current price");
        }   else {
            throw new UserDoesNotHaveAccessException();
        }
    }

    public Bet update(BetDTO betDTO, Long id) {
        Bet existBet = betsRepository.getReferenceById(id);

        if (!existBet.getLot().isAvailable()) {
            throw new UnableToCreateException("Unable to update the bet. The auction has probably ended.");
        }

        if (betDTO.price() != 0 && betDTO.price() != existBet.getPrice() && betDTO.price() > existBet.getLot().getCurrentPrice()) {
            existBet.setPrice(betDTO.price());
            lotService.setCurrentPrice(existBet.getLot(), betDTO.price());
        }

        return betsRepository.save(existBet);
    }

    public boolean delete(Long id) {
        Bet bet = betsRepository.getReferenceById(id);
        User sender = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (bet.getUser().equals(sender)) {
            if (!bet.getLot().isAvailable()) {
                throw new UnableToDeleteException("Unable to delete bet. The auction has probably ended.");
            }

        }   else if (sender.getRole().equals("owner") || sender.getRole().equals("admin")) {
            lotService.removeBet(bet.getLot());
            userService.removeBetFromUser(bet.getUser());
            betsRepository.delete(bet);
            return true;
        }
        return false;
    }

    public List<Bet> getBetsByLot(Lot lot) {
        return betsRepository.findByLot(lot);
    }
}