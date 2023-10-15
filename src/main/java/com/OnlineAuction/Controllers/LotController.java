package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Services.LotServices;
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

    private final LotServices lotServices;

    @Autowired
    public LotController(LotServices lotServices) {
        this.lotServices = lotServices;
    }

    @GetMapping
    public List<LotDTO> getLots() {
        return lotServices.getLots().stream().map(lot -> new LotDTO(lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimum_price())).toList();
    }

    @GetMapping("/{id}")
    public LotDTO getLotById(@PathVariable("id") Long id) {
        try {
            Lot receivedLot = lotServices.getLotById(id);
            return new LotDTO(receivedLot.getName(), receivedLot.getDescription(), receivedLot.getImage(), receivedLot.getMinimum_price());
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }

    @PostMapping
    public LotDTO create(@RequestBody LotDTO lotDTO) {
        try {
            Lot createdLot = lotServices.create(lotDTO);
            return new LotDTO(createdLot.getName(), createdLot.getDescription(), createdLot.getImage(), createdLot.getMinimum_price());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The creator not found");
        }
    }

    @PutMapping("/{id}")
    public LotDTO update(@RequestBody LotDTO lotDTO, @PathVariable("id") Long id_lot) {
        try {
            Lot updatedLot = lotServices.update(lotDTO, id_lot);
            return new LotDTO(updatedLot.getName(), updatedLot.getDescription(), updatedLot.getImage(), updatedLot.getMinimum_price());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        try {
            return lotServices.delete(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lot not found");
        }
    }
}