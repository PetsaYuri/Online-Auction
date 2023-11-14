package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.ResultOfActionDTO;
import com.OnlineAuction.Models.ResultOfAuction;
import com.OnlineAuction.Services.ResultOfAuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultsOfAuctions")
public class ResultOfAuctionController {

    private final ResultOfAuctionService resultOfAuctionService;

    @Autowired
    public ResultOfAuctionController(ResultOfAuctionService resultOfAuctionService) {
        this.resultOfAuctionService = resultOfAuctionService;
    }

    @GetMapping
    public List<ResultOfAuction> getAll() {
        return resultOfAuctionService.getAll();
    }

    @GetMapping("/{id}")
    public ResultOfAuction getOne(@PathVariable("id") Long id) {
        return resultOfAuctionService.getResultById(id);
    }

    @PostMapping
    public ResultOfAuction create(@RequestBody ResultOfActionDTO resultOfActionDTO) {
        return resultOfAuctionService.create(resultOfActionDTO.auction().getId());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return resultOfAuctionService.delete(id);
    }
}