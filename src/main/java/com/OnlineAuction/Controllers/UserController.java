package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Exceptions.Users.EmailAlreadyUsesException;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.UserServices;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServices userServices;

    @Autowired
    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/init")
    public String init() {
        return "CI/CD enabled";
    }

    @GetMapping
    public List<User> getUsers() {
        return userServices.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) {
        try {
            User user = userServices.getOne(id);
            return new UserDTO(user.getFirst_name(), user.getLast_name(), user.getEmail(), null, user.getImage());
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO userDTO) {
        try {
            User user = userServices.create(userDTO);
            return new UserDTO(user.getFirst_name(), user.getLast_name(), user.getEmail(), null, user.getImage());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EmailAlreadyUsesException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public UserDTO update(@RequestBody UserDTO userDTO, @PathVariable("id") Long id_user) {
        try {
            User updatedUser = userServices.update(userDTO, id_user);
            return new UserDTO(updatedUser.getFirst_name(), updatedUser.getLast_name(), updatedUser.getEmail(), null, updatedUser.getImage());
        }   catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The body is not fully written");
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        try {
            return userServices.delete(id);
        }   catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}