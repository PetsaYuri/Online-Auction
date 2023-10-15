package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionsRepository extends JpaRepository<Auction, Long> {}