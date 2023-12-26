package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.MainService;
import com.OnlineAuction.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MainController {

    private final MainService mainService;

    private final UserService userService;

    @Autowired
    public MainController(MainService mainService, UserService userService) {
        this.mainService = mainService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody UserDTO userDTO) {
        User user = userService.create(userDTO);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping("/login")
    public UserDTO login() {
        User user = userService.getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping("/uploadImage")
    public String uploadImageToServer(@RequestParam("file")MultipartFile file) {
        return mainService.uploadImage(file);
    }
}
