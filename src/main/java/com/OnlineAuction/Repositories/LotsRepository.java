package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotsRepository extends JpaRepository<Lot, Long> {}