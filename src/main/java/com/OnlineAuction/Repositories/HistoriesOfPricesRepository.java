package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.HistoryOfPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoriesOfPricesRepository extends JpaRepository<HistoryOfPrice, Long> {}