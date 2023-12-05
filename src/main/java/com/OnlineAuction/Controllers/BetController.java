package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/bets")
public class BetController {

    private final BetService betService;

    @Autowired
    public BetController(BetService betService) {
        this.betService = betService;
    }

    @GetMapping
    public List<Bet> getAll(@RequestParam(value = "size", defaultValue = "10") int size,
                            @RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "date_start", required = false) Timestamp dateStart,
                            @RequestParam(value = "date_end", required = false) Timestamp dateEnd) {
        Pageable pageable = PageRequest.of(page, size);
        if (dateStart != null && dateEnd != null) {
            return betService.getAllInBetweenDays(dateStart, dateEnd, pageable);
        }

        if (dateStart != null) {
            return betService.getAllAfterDate(dateStart, pageable);
        }
        if (dateEnd != null) {
            return betService.getAllBeforeDate(dateEnd, pageable);
        }

        return betService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public Bet getOne(@PathVariable("id") Long id) {
        return betService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Bet add(@RequestBody BetDTO betDTO) {
        return betService.add(betDTO, betDTO.lot().getId());
    }

    @PutMapping("/{id}")
    public Bet update(@PathVariable("id") Long id, @RequestBody BetDTO betDTO) {
        return betService.update(betDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return betService.delete(id);
    }
}