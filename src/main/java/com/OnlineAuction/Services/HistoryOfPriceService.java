package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.Exceptions.UnableToCreateException;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.HistoriesOfPricesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryOfPriceService {

    private final HistoriesOfPricesRepository historiesRepository;

    private final UserService userService;

    private final LotService lotService;

    @Autowired
    public HistoryOfPriceService(HistoriesOfPricesRepository historiesRepository, UserService userService, LotService lotService) {
        this.historiesRepository = historiesRepository;
        this.userService = userService;
        this.lotService = lotService;
    }

    public HistoryOfPrice getById(Long id) {
        return historiesRepository.getReferenceById(id);
    }

    public List<HistoryOfPrice> getAll() {
        return historiesRepository.findAll();
    }

    public HistoryOfPrice add(HistoryOfPriceDTO historyDTO, Long id) {
        User user = userService.getOne(1L);
        Lot lot = lotService.getLotById(id);

        if (historyDTO.price() > lot.getCurrent_price()) {
            HistoryOfPrice newHistory = new HistoryOfPrice(historyDTO, user, lot);
            historiesRepository.save(newHistory);
            lotService.setCurrentPrice(newHistory.getLot(), newHistory.getPrice());
            lotService.setHistoryOfPrice(newHistory.getLot(), newHistory);
            userService.setHistoryOfPrices(user, newHistory);
            return newHistory;
        }
        throw new UnableToCreateException("Unable to create bet. Price is lower than current price");
    }

    public HistoryOfPrice update(HistoryOfPriceDTO historyDTO, Long id) {
        HistoryOfPrice existHistory = historiesRepository.getReferenceById(id);

        if (historyDTO.price() != 0 && historyDTO.price() != existHistory.getPrice() && historyDTO.price() > existHistory.getLot().getCurrent_price()) {
            existHistory.setPrice(historyDTO.price());
            lotService.setCurrentPrice(existHistory.getLot(), historyDTO.price());
        }

        return historiesRepository.save(existHistory);
    }

    public boolean delete(Long id) {
        if (!historiesRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find History of price with id " + id);
        }

        HistoryOfPrice history = historiesRepository.getReferenceById(id);
        lotService.unsetHistoryOfPrice(history.getLot());
        userService.unsetHistoryOfPrices(history.getUser());
        historiesRepository.delete(history);
        return true;
    }

    public void deleteLotFromHistoryOfPrice(Lot lot) {
        HistoryOfPrice history = lot.getHistoryOfPrice();
        history.setLot(null);
        historiesRepository.save(history);
    }
}