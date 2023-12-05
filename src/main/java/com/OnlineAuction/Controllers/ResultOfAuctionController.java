package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.ResultOfActionDTO;
import com.OnlineAuction.Models.ResultOfAuction;
import com.OnlineAuction.Services.ResultOfAuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    public List<ResultOfAuction> getAll(@RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "q", required = false) String query,
                                        @RequestParam(value = "date_start", required = false)Timestamp dateStart,
                                        @RequestParam(value = "date_end", required = false) Timestamp dateEnd) {
        Pageable pageable = PageRequest.of(page, size);

        if (query != null && dateStart != null && dateEnd != null) {
            return resultOfAuctionService.getByNameAuctionAndStartAndEnds(query, dateStart, dateEnd, pageable);
        }

        if (query != null && dateStart != null) {
            return resultOfAuctionService.getByNameAuctionAndStart(query, dateStart, pageable);
        }

        if (query != null && dateEnd != null) {
            return resultOfAuctionService.getByNameAuctionAndEnds(query, dateEnd, pageable);
        }

        if (query != null) {
            return resultOfAuctionService.getByNameAuction(query, pageable);
        }

        if (dateStart != null && dateEnd != null) {
            return resultOfAuctionService.getByStartAndEnds(dateStart, dateEnd, pageable);
        }

        if(dateStart != null) {
            return resultOfAuctionService.getAfterStart(dateStart, pageable);
        }

        if (dateEnd != null) {
            return resultOfAuctionService.getBeforeEnd(dateEnd, pageable);
        }

        return resultOfAuctionService.getAll(pageable);
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