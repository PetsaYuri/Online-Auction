package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.ResultOfAuction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AuctionsRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByResultOfAuction(ResultOfAuction resultOfAuction);

    Page<Auction> findByTitleContainsIgnoreCaseAndResultOfAuction(String title, ResultOfAuction resultOfAuction, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCase(String title, Pageable pageable);

    Page<Auction> findByResultOfAuction(ResultOfAuction resultOfAuction, Pageable pageable);

    Page<Auction> findByStartAfterAndEndsBeforeAndResultOfAuction(Timestamp start, Timestamp ends, ResultOfAuction resultOfAuction, Pageable pageable);

    Page<Auction> findByStartAfterAndEndsBefore(Timestamp start, Timestamp ends, Pageable pageable);

    Page<Auction> findByStartAfterAndResultOfAuction(Timestamp start, ResultOfAuction resultOfAuction, Pageable pageable);

    Page<Auction> findByStartAfter(Timestamp start, Pageable pageable);

    Page<Auction> findByEndsBeforeAndResultOfAuction(Timestamp ends, ResultOfAuction resultOfAuction, Pageable pageable);

    Page<Auction> findByEndsBefore(Timestamp ends, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndResultOfAuctionAndStartAfterAndEndsBefore(String title, ResultOfAuction resultOfAuction, Timestamp start,
                                                                                            Timestamp ends, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndStartAfterAndEndsBefore(String title,Timestamp start, Timestamp ends, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndResultOfAuctionAndStartAfter(String title, ResultOfAuction resultOfAuction, Timestamp date_start, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndStartAfter(String title, Timestamp date_start, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndResultOfAuctionAndEndsBefore(String title, ResultOfAuction resultOfAuction, Timestamp ends, Pageable pageable);

    Page<Auction> findByTitleContainsIgnoreCaseAndEndsBefore(String title, Timestamp ends, Pageable pageable);
}