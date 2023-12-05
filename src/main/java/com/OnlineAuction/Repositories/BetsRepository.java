package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BetsRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByLot(Lot lot);

    Page<Bet> findByDateBetween(Timestamp start, Timestamp end, Pageable pageable);

    Page<Bet> findByDateAfter(Timestamp date, Pageable pageable);

    Page<Bet> findByDateBefore(Timestamp date, Pageable pageable);
}