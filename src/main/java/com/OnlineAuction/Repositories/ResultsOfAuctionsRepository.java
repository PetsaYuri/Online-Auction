package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.ResultOfAuction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultsOfAuctionsRepository extends JpaRepository<ResultOfAuction, Long> {}