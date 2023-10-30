package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.HistoriesOfPricesRepository;
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

    public HistoryOfPrice add(HistoryOfPriceDTO historyDTO, Lot lot) {
        User user = userService.getOne(1L);

        if (historyDTO.price() > lot.getCurrent_price()) {
            lot = lotService.getLotById(lot.getId());
            HistoryOfPrice newHistory = new HistoryOfPrice(historyDTO, user, lot);
            historiesRepository.save(newHistory);
            lotService.setHistoryOfPrice(newHistory.getLot(), newHistory);
            userService.setHistoryOfPrices(user, newHistory);
            return newHistory;
        }
        throw new RuntimeException();
    }

    public HistoryOfPrice update(HistoryOfPriceDTO historyDTO, Long id) {
        HistoryOfPrice existHistory = historiesRepository.getReferenceById(id);

        if (historyDTO.price() != 0 && historyDTO.price() != existHistory.getPrice() && historyDTO.price() > existHistory.getLot().getCurrent_price()) {
            existHistory.setPrice(historyDTO.price());
        }

        return historiesRepository.save(existHistory);
    }

    public boolean delete(Long id) {
        HistoryOfPrice history = historiesRepository.getReferenceById(id);
        historiesRepository.delete(history);
        return true;
    }
}