package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Lot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotsRepository extends JpaRepository<Lot, Long> {

    Page<Lot> findByNameContainsIgnoreCase(String name, Pageable pageable);

    Page<Lot> findByIsAvailable(boolean isAvailable, Pageable pageable);

    Page<Lot> findByCurrentPriceBetween(int start, int end, Pageable pageable);

    Page<Lot> findByCurrentPriceGreaterThan(int price, Pageable pageable);

    Page<Lot> findByCurrentPriceLessThan(int price, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceBetween(String name, boolean isAvailable, int startPrice, int endPrice, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceGreaterThan(String name, boolean isAvailable, int price, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndIsAvailableAndCurrentPriceLessThan(String name, boolean isAvailable, int price, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndCurrentPriceBetween(String name, int startPrice, int endPrice, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndCurrentPriceGreaterThan(String name, int price, Pageable pageable);

    Page<Lot> findByNameContainsIgnoreCaseAndCurrentPriceLessThan(String name, int price, Pageable pageable);

    Page<Lot> findByIsAvailableAndCurrentPriceBetween(boolean isAvailable, int startPrice, int endPrice, Pageable pageable);

    Page<Lot> findByIsAvailableAndCurrentPriceGreaterThan(boolean isAvailable, int price, Pageable pageable);

    Page<Lot> findByIsAvailableAndCurrentPriceLessThan(boolean isAvailable, int price, Pageable pageable);
}