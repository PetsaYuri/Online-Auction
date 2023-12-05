package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.DTO.LotDTO;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<LotDTO> getAll(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size,
                               @RequestParam(value = "q", required = false) String query, @RequestParam(value = "available", required = false) String isAvailable,
                               @RequestParam(value = "price_greater_than", required = false) String priceGreaterThan,
                               @RequestParam(value = "price_less_than", required = false) String priceLessThan) {
        Pageable pageable = PageRequest.of(page, size);

        if (query != null && isAvailable != null && priceGreaterThan != null && priceLessThan != null) {
            return lotService.getByNameAndAvailableAndCurrentPriceInRange(query, Boolean.parseBoolean(isAvailable), Integer.parseInt(priceGreaterThan),
                    Integer.parseInt(priceLessThan), pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                    lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (query != null && isAvailable != null && priceGreaterThan != null) {
            return lotService.getByNameAndAvailableAndCurrentPriceGreaterThan(query, Boolean.parseBoolean(isAvailable), Integer.parseInt(priceGreaterThan),
                    pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                    lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (query != null && isAvailable != null && priceLessThan != null) {
            return lotService.getByNameAndAvailableAndCurrentPriceLessThan(query, Boolean.parseBoolean(isAvailable), Integer.parseInt(priceLessThan),
                    pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                    lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (query != null && priceGreaterThan != null && priceLessThan != null) {
            return lotService.getByNameAndCurrentPriceInRange(query, Integer.parseInt(priceGreaterThan), Integer.parseInt(priceLessThan), pageable)
                    .stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(),
                            lot.getCategory())).toList();
        }

        if (query != null && priceGreaterThan != null) {
            return lotService.getByNameAndCurrentPriceGreaterThan(query, Integer.parseInt(priceGreaterThan), pageable).stream().map(lot ->
                    new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (query != null && priceLessThan != null) {
            return lotService.getByNameAndCurrentPriceLessThan(query, Integer.parseInt(priceLessThan), pageable).stream().map(lot ->
                    new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (isAvailable != null && priceGreaterThan != null && priceLessThan != null) {
            return lotService.getByAvailableAndCurrentPriceInRange(Boolean.parseBoolean(isAvailable), Integer.parseInt(priceGreaterThan),
                    Integer.parseInt(priceLessThan), pageable).stream().map(lot ->
                    new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (isAvailable != null && priceGreaterThan != null) {
            return lotService.getByAvailableAndCurrentPriceGreaterThan(Boolean.parseBoolean(isAvailable), Integer.parseInt(priceGreaterThan), pageable)
                    .stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (isAvailable != null && priceLessThan != null) {
            return lotService.getByAvailableAndCurrentPriceLessThan(Boolean.parseBoolean(isAvailable), Integer.parseInt(priceLessThan), pageable)
                    .stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (query != null) {
            return lotService.getByName(query, pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                    lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (isAvailable != null) {
            return lotService.getByAvailable(Boolean.parseBoolean(isAvailable), pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(),
                    lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (priceGreaterThan != null && priceLessThan != null) {
            return lotService.getByCurrentPriceInRange(Integer.parseInt(priceGreaterThan), Integer.parseInt(priceLessThan), pageable).stream().map(lot ->
                    new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (priceGreaterThan != null) {
            return lotService.getByCurrentPriceGreaterThan(Integer.parseInt(priceGreaterThan), pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(),
                    lot.getDescription(), lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        if (priceLessThan != null) {
            return lotService.getByCurrentPriceLessThan(Integer.parseInt(priceLessThan), pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(),
                    lot.getImage(), lot.getMinimumPrice(), lot.getCategory())).toList();
        }

        return lotService.getAll(pageable).stream().map(lot -> new LotDTO(lot.getId(), lot.getName(), lot.getDescription(), lot.getImage(),
                lot.getMinimumPrice(), lot.getCategory())).toList();
    }

    @GetMapping("/{id}")
    public LotDTO getLotById(@PathVariable("id") Long id) {
        Lot receivedLot = lotService.getLotById(id);
        return new LotDTO(receivedLot.getId(), receivedLot.getName(), receivedLot.getDescription(), receivedLot.getImage(),
                receivedLot.getMinimumPrice(), receivedLot.getCategory());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LotDTO create(@RequestBody LotDTO lotDTO) {
        Lot createdLot = lotService.create(lotDTO);
        return new LotDTO(createdLot.getId(), createdLot.getName(), createdLot.getDescription(), createdLot.getImage(),
                createdLot.getMinimumPrice(), createdLot.getCategory());
    }

    @PutMapping("/{id}")
    public LotDTO update(@RequestBody LotDTO lotDTO, @PathVariable("id") Long id_lot) {
        Lot updatedLot = lotService.update(lotDTO, id_lot);
        return new LotDTO(updatedLot.getId(), updatedLot.getName(), updatedLot.getDescription(), updatedLot.getImage(),
                updatedLot.getMinimumPrice(), updatedLot.getCategory());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return lotService.delete(id);
    }

    @PostMapping("/{idLot}/make_bet")
    public Bet makeABet(@PathVariable("idLot") Long idLot, @RequestBody BetDTO betDTO) {
        return lotService.makeBet(betDTO, idLot);
    }
}