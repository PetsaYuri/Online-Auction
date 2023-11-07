package com.OnlineAuction.DTO;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {
}
