package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.ResultOfAuction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultsOfAuctionsRepository extends JpaRepository<ResultOfAuction, Long> {}