package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.BetDTO;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Services.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public List<Bet> getAll() {
        return betService.getAll();
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