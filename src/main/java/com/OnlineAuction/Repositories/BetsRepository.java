package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetsRepository extends JpaRepository<Bet, Long> {
    List<Bet> findByLot(Lot lot);
}