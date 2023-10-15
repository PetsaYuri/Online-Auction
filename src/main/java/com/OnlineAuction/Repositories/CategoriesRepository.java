package com.OnlineAuction.Repositories;

import com.OnlineAuction.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepository extends JpaRepository<Category, Long> {}