package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Services.HistoryOfPriceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        try {
            return historyService.getById(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History of price not found");
        }
    }

    @PostMapping
    public HistoryOfPrice add(@RequestBody HistoryOfPriceDTO historyDTO) {
        try {
            return historyService.add(historyDTO, historyDTO.lot());
        } /*  catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }*/   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public HistoryOfPrice update(@PathVariable("id") Long id, @RequestBody HistoryOfPriceDTO historyDTO) {
        try {
            return historyService.update(historyDTO, id);
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History of price not found");
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        try {
            return historyService.delete(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "History of price not found");
        }
    }
}