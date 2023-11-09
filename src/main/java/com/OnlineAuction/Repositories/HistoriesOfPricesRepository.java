package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.HistoryOfPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriesOfPricesRepository extends JpaRepository<HistoryOfPrice, Long> {}