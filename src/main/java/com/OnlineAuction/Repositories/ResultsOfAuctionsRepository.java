package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.ResultOfAuction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface ResultsOfAuctionsRepository extends JpaRepository<ResultOfAuction, Long> {

    Page<ResultOfAuction> findByNameAuctionContainsIgnoreCase(String name, Pageable pageable);

    Page<ResultOfAuction> findByStartAfterAndEndsBefore(Timestamp start, Timestamp ends, Pageable pageable);

    Page<ResultOfAuction> findByStartAfter(Timestamp start, Pageable pageable);

    Page<ResultOfAuction> findByEndsBefore(Timestamp ends, Pageable pageable);

    Page<ResultOfAuction> findByNameAuctionContainsIgnoreCaseAndStartAfterAndEndsBefore(String name, Timestamp start, Timestamp ends, Pageable pageable);

    Page<ResultOfAuction> findByNameAuctionContainsIgnoreCaseAndStartAfter(String name, Timestamp start, Pageable pageable);

    Page<ResultOfAuction> findByNameAuctionContainsIgnoreCaseAndEndsBefore(String name, Timestamp ends, Pageable pageable);
}