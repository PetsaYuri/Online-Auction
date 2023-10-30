package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.AuctionDTO;
import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Services.AuctionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
    }

    @GetMapping
    public List<AuctionDTO> getAll(@RequestHeader(name = "X-Time-Zone", defaultValue = "Europe/Kiev",required = false) String timezone) {
      //  simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return auctionService.getAll().stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(), 0,
               simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
    }

    @GetMapping("/{id}")
    public AuctionDTO getOneById(@PathVariable("id") Long id) {
        try {
            Auction auction = auctionService.getOneById(id);
            return new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(), 0,
                    simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots());
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found");
        }
    }

    @PostMapping
    public AuctionDTO create(@RequestBody AuctionDTO auctionDTO) {
        try {
            Auction newAuction = auctionService.create(auctionDTO);
            return new AuctionDTO(newAuction.getId(), newAuction.getTitle(), newAuction.getDescription(), 0,
                    simpleDateFormat.format(newAuction.getStart()), simpleDateFormat.format(newAuction.getEnds()), newAuction.getLots());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }
    }

    @PutMapping("/{id}")
    public AuctionDTO update(@RequestBody AuctionDTO auctionDTO, @PathVariable("id") Long id) {
        try {
            Auction updatedAction = auctionService.update(id, auctionDTO);
            return new AuctionDTO(updatedAction.getId(), updatedAction.getTitle(), updatedAction.getDescription(), 0,
                    simpleDateFormat.format(updatedAction.getStart()), simpleDateFormat.format(updatedAction.getEnds()), updatedAction.getLots());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found");
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        try {
            return auctionService.delete(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found");
        }
    }
}