package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Services.AuctionService;
import com.OnlineAuction.Services.LotService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
public class LotController {

    private final LotService lotService;
    private final AuctionService auctionService;

    @Autowired
    public LotController(LotService lotService, AuctionService auctionService) {
        this.lotService = lotService;
        this.auctionService = auctionService;
    }

    @GetMapping
    public List<LotDTO> getLots() {
        return lotService.getLots().stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimum_price())).toList();
    }

    @GetMapping("/{id}")
    public LotDTO getLotById(@PathVariable("id") Long id) {
        try {
            Lot receivedLot = lotService.getLotById(id);
            return new LotDTO(receivedLot.getId(), receivedLot.getName(), receivedLot.getDescription(), receivedLot.getImage(), receivedLot.getMinimum_price());
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }

    @PostMapping
    public LotDTO create(@RequestBody LotDTO lotDTO) {
        try {
            Lot createdLot = lotService.create(lotDTO);
            if (lotService.getLotsWithoutAuction().size() >= auctionService.getQuantity()) {
                System.out.println("1111");
                auctionService.autoCreate();
            }
            return new LotDTO(createdLot.getId(), createdLot.getName(), createdLot.getDescription(), createdLot.getImage(), createdLot.getMinimum_price());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The creator not found");
        }
    }

    @PutMapping("/{id}")
    public LotDTO update(@RequestBody LotDTO lotDTO, @PathVariable("id") Long id_lot) {
        try {
            Lot updatedLot = lotService.update(lotDTO, id_lot);
            return new LotDTO(updatedLot.getId(), updatedLot.getName(), updatedLot.getDescription(), updatedLot.getImage(), updatedLot.getMinimum_price());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        try {
            return lotService.delete(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }
}