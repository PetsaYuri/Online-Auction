package com.OnlineAuction.Services;

import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.ResultOfAuction;
import com.OnlineAuction.Repositories.AuctionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionResultService {

    private final AuctionsRepository auctionsRepository;

    @Autowired
    public AuctionResultService(AuctionsRepository auctionsRepository) {
        this.auctionsRepository = auctionsRepository;
    }

    public Auction getOneById(long id) {
        return auctionsRepository.getReferenceById(id);
    }

    public void setResultOfAuction(ResultOfAuction resultOfAuction) {
        Auction auction = resultOfAuction.getAuction();
        auction.setResultOfAuction(resultOfAuction);
        auctionsRepository.save(auction);
    }

    public void removeResultOfAuction(Auction auction) {
        auction.setResultOfAuction(null);
        auctionsRepository.save(auction);
    }
}
