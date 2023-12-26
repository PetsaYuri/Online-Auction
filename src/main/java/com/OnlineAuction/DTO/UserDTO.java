package com.OnlineAuction.DTO;

import com.OnlineAuction.Models.Lot;

import java.util.List;

public record UserDTO(String first_name, String last_name, String email, String password, String image, boolean isBlocked, String role, List<Lot> listOfCreatedLots,
                      List<Lot> listLotOfWinning) {
}
