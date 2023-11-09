package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
public class LotController {

    private final LotService lotService;

    @Autowired
    public LotController(LotService lotService) {
        this.lotService = lotService;
    }

    @GetMapping
    public List<LotDTO> getLots() {
        return lotService.getLots().stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                lot.getMinimum_price(), lot.getCategory())).toList();
    }

    @GetMapping("/{id}")
    public LotDTO getLotById(@PathVariable("id") Long id) {
        Lot receivedLot = lotService.getLotById(id);
        return new LotDTO(receivedLot.getId(), receivedLot.getName(), receivedLot.getDescription(), receivedLot.getImage(),
                receivedLot.getMinimum_price(), receivedLot.getCategory());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LotDTO create(@RequestBody LotDTO lotDTO) {
        Lot createdLot = lotService.create(lotDTO);
        return new LotDTO(createdLot.getId(), createdLot.getName(), createdLot.getDescription(), createdLot.getImage(),
                createdLot.getMinimum_price(), createdLot.getCategory());
    }

    @PutMapping("/{id}")
    public LotDTO update(@RequestBody LotDTO lotDTO, @PathVariable("id") Long id_lot) {
        Lot updatedLot = lotService.update(lotDTO, id_lot);
        return new LotDTO(updatedLot.getId(), updatedLot.getName(), updatedLot.getDescription(), updatedLot.getImage(),
                updatedLot.getMinimum_price(), updatedLot.getCategory());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return lotService.delete(id);
    }

    @PostMapping("/{idLot}/make_bet")
    public HistoryOfPrice makeABet(@PathVariable("idLot") Long idLot, @RequestBody HistoryOfPriceDTO historyOfPriceDTO) {
        return lotService.makeBet(historyOfPriceDTO, idLot);
    }
}