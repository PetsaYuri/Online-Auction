package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.AuctionDTO;
import com.OnlineAuction.DTO.AuctionPropertiesDTO;
import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Services.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
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
    public List<AuctionDTO> getAll(@RequestHeader(name = "X-Time-Zone", defaultValue = "Europe/Kiev",required = false) String timezone,
                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                   @RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "q", required = false) String query,
                                   @RequestParam(name = "onlyActive", defaultValue = "false") boolean active,
                                   @RequestParam(name = "date_start", required = false)Timestamp date_start,
                                   @RequestParam(name = "date_end", required = false) Timestamp date_end) {
        //  simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        Pageable pageable = PageRequest.of(page, size);

        if (query != null && date_start != null && date_end != null) {
            return auctionService.getByTitleAndActiveAndStartAndEnd(query, active, date_start, date_end, pageable).stream().map(auction -> new AuctionDTO(auction.getId(),
                    auction.getTitle(), auction.getDescription(), 0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()),
                    auction.getLots())).toList();
        }

        if (query != null && date_start != null) {
            return auctionService.getByTitleAndActiveAndStart(query, active, date_start, pageable).stream().map(auction -> new AuctionDTO(auction.getId(),
                    auction.getTitle(), auction.getDescription(), 0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        if (query != null && date_end != null) {
            return auctionService.getByTitleAndActiveAndEnd(query, active, date_end, pageable).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                    0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        if (query != null) {
            return auctionService.getByTitleAndActive(query, active, pageable).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                    0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        if (date_start != null && date_end != null) {
            return auctionService.getByStartAndEndAndActive(date_start, date_end, active, pageable).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                    0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        if (date_start != null) {
            return auctionService.getAfterStartAndActive(date_start, active, pageable).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                    0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        if (date_end != null) {
            return auctionService.getBeforeEndAndActive(date_end, active, pageable).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                    0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
        }

        return auctionService.getByActive(pageable, active).stream().map(auction -> new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(),
                0, simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots())).toList();
    }

    @GetMapping("/{id}")
    public AuctionDTO getOneById(@PathVariable("id") Long id) {
        Auction auction = auctionService.getOneById(id);
        return new AuctionDTO(auction.getId(), auction.getTitle(), auction.getDescription(), 0,
                simpleDateFormat.format(auction.getStart()), simpleDateFormat.format(auction.getEnds()), auction.getLots());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuctionDTO create(@RequestBody AuctionDTO auctionDTO) {
        Auction newAuction = auctionService.create(auctionDTO);
        return new AuctionDTO(newAuction.getId(), newAuction.getTitle(), newAuction.getDescription(), 0,
                simpleDateFormat.format(newAuction.getStart()), simpleDateFormat.format(newAuction.getEnds()), newAuction.getLots());
    }

    @PutMapping("/{id}")
    public AuctionDTO update(@RequestBody AuctionDTO auctionDTO, @PathVariable("id") Long id) {
        Auction updatedAction = auctionService.update(id, auctionDTO);
        return new AuctionDTO(updatedAction.getId(), updatedAction.getTitle(), updatedAction.getDescription(), 0,
                simpleDateFormat.format(updatedAction.getStart()), simpleDateFormat.format(updatedAction.getEnds()), updatedAction.getLots());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return auctionService.delete(id);
    }

    @GetMapping("/properties")
    public AuctionPropertiesDTO getAuctionProperties() {
        return new AuctionPropertiesDTO(auctionService.getQuantity(), auctionService.getDuration());
    }

    @PutMapping("/properties")
    public AuctionPropertiesDTO updateAuctionProperties(@RequestBody AuctionPropertiesDTO auctionPropertiesDTO) {
        return auctionService.updateAuctionProperties(auctionPropertiesDTO);
    }

    @PostMapping("/{id}/earlyEnd")
    public AuctionDTO earlyEndOfAuction(@PathVariable("id") Long id) {
        Auction auction = auctionService.getOneById(id);
        Auction updatedAuction = auctionService.performResultsOfAuction(auction);
        return new AuctionDTO(updatedAuction.getId(), updatedAuction.getTitle(), updatedAuction.getDescription(), 0, updatedAuction.getStart().toString(),
                updatedAuction.getEnds().toString(), updatedAuction.getLots());
    }
}