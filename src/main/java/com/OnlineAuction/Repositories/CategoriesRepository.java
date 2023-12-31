package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {

    Page<Category> findByTitleContainsIgnoreCase(String title, Pageable pageable);
}