package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponseDTO> handlingEntityNotFoundException(RuntimeException ex, WebRequest request) {
        String error = "Entity not found";
        String message = ex.getMessage() == null
                ? "Entity not found"
                : ex.getMessage().replaceAll("com\\.OnlineAuction\\.Models\\.", "");
        String path = request.getDescription(false).replaceAll("uri=", "");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), error, message, path));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponseDTO> handlingDataIntegrityViolationException(RuntimeException ex, WebRequest request) {
        String error = "Data integrity violation";
        String message = ex.getMessage() == null
                ? "The body is not fully written"
                : ex.getMessage().replaceAll("com\\.OnlineAuction\\.Models\\.", "");
        String path = request.getDescription(false).replaceAll("uri=", "");

        return ResponseEntity.badRequest().body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), error, message, path));
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponseDTO> handlingNullPointerException(RuntimeException ex, WebRequest request) {
        String error = "Null pointer";
        String message = ex.getMessage();
        String path = request.getDescription(false).replaceAll("uri=", "");
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), error, message, path));
    }

    @ExceptionHandler(MultipartException.class)
    protected ResponseEntity<ErrorResponseDTO> handlingMultipartException(RuntimeException ex, WebRequest request) {
        String error = "Multipart";
        String message = ex.getMessage();
        String path = request.getDescription(false).replaceAll("uri=", "");
        return ResponseEntity.badRequest().body(new ErrorResponseDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), error, message, path));
    }
}