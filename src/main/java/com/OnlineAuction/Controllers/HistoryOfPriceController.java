package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Services.HistoryOfPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historyPrice")
public class HistoryOfPriceController {

    private final HistoryOfPriceService historyService;

    @Autowired
    public HistoryOfPriceController(HistoryOfPriceService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public List<HistoryOfPrice> getAll() {
        return historyService.getAll();
    }

    @GetMapping("/{id}")
    public HistoryOfPrice getOne(@PathVariable("id") Long id) {
        return historyService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoryOfPrice add(@RequestBody HistoryOfPriceDTO historyDTO) {
        return historyService.add(historyDTO, historyDTO.lot());
    }

    @PutMapping("/{id}")
    public HistoryOfPrice update(@PathVariable("id") Long id, @RequestBody HistoryOfPriceDTO historyDTO) {
        return historyService.update(historyDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return historyService.delete(id);
    }
}