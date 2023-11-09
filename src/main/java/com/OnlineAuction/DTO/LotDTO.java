package com.OnlineAuction.DTO;

import com.OnlineAuction.Models.Category;

public record LotDTO(Long id, String name, String description, String image, int minimum_price, Category category) {
}
