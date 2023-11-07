package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) {
        User user = userService.getOne(id);
        return new UserDTO(user.getFirst_name(), user.getLast_name(), user.getEmail(), null, user.getImage());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = userService.create(userDTO);
        return new UserDTO(user.getFirst_name(), user.getLast_name(), user.getEmail(), null, user.getImage());
    }

    @PutMapping("/{id}")
    public UserDTO update(@RequestBody UserDTO userDTO, @PathVariable("id") Long id_user) {
        User updatedUser = userService.update(userDTO, id_user);
        return new UserDTO(updatedUser.getFirst_name(), updatedUser.getLast_name(), updatedUser.getEmail(), null, updatedUser.getImage());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }
}