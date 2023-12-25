package com.OnlineAuction.DTO;

import com.OnlineAuction.Models.Auction;
import com.OnlineAuction.Models.Category;
import com.OnlineAuction.Models.User;

public record LotDTO(Long id, String name, String description, String image, int current_price, int minimum_price, boolean isAvailable, Category category,
                     Auction auction, User creator, User winner) {
}
