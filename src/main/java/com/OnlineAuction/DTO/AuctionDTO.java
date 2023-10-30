package com.OnlineAuction.DTO;

import com.OnlineAuction.Models.Lot;

import java.sql.Timestamp;
import java.util.List;

public record AuctionDTO(Long id, String title, String description, int number_days, String start, String end, List<Lot> lots) {}