package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.ResultOfAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionsRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByResultOfAuction(ResultOfAuction resultOfAuction);
}