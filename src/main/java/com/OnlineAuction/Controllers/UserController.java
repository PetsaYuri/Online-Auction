package com.OnlineAuction.Controllers;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<User> getUsers(@RequestParam(value = "size", defaultValue = "10") int size, @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "q", required = false) String query, @RequestParam(value = "only_blocked", required = false) String blocked,
                               @RequestParam(value = "role", required = false) String role) {
        Pageable pageable = PageRequest.of(page, size);

        if (query != null) {
            if (query.contains("@")) {
                return userService.getByEmail(query, pageable);
            }
        }

        if(query != null && blocked != null && role != null) {
            String[] arr = query.split(" ");
            return arr.length == 2
                    ? userService.getByFirstNameAndLastNameAndBlockedAndRole(arr[0], arr[1], Boolean.parseBoolean(blocked), role, pageable)
                    : userService.getByLastNameAndBlockedAndRole(query, Boolean.parseBoolean(blocked), role, pageable);
        }

        if (query != null && blocked != null) {
            String[] arr = query.split(" ");
            return arr.length == 2
                    ? userService.getByFirstNameAndLastNameAndBlocked(arr[0], arr[1], Boolean.parseBoolean(blocked), pageable)
                    : userService.getByLastNameAndBlocked(query, Boolean.parseBoolean(blocked), pageable);
        }

        if (query != null && role != null) {
            String[] arr = query.split(" ");
            return arr.length == 2
                    ? userService.getByFirstNameAndLastNameAndRole(arr[0], arr[1], role, pageable)
                    : userService.getByLastNameAndRole(query, role, pageable);
        }

        if (blocked != null && role != null) {
            userService.getByBlockedAndRole(Boolean.parseBoolean(blocked), role, pageable);
        }

        if (query != null) {
            String[] arr = query.split(" ");
            return arr.length == 2 ? userService.getByFirstNameAndLastName(arr[0], arr[1], pageable) : userService.getByLastName(query, pageable);
        }

        if (role != null) {
            return userService.getByRole(role, pageable);
        }

        if (blocked != null) {
            return userService.getByBlocked(Boolean.parseBoolean(blocked), pageable);
        }

        return userService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) {
        User user = userService.getOne(id);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = userService.create(userDTO);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), userDTO.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PutMapping("/{id}")
    public UserDTO update(@RequestBody UserDTO userDTO, @PathVariable("id") Long id_user) {
        User updatedUser = userService.update(userDTO, id_user);
        return new UserDTO(updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getEmail(), null, updatedUser.getImage(), updatedUser.isBlocked(),
                updatedUser.getRole(), updatedUser.getListOfCreatedLots(), updatedUser.getListLotOfWinning());
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @PostMapping("/{id}/setAdmin")
    public UserDTO setAdmin(@PathVariable("id") Long idUser) {
        User user = userService.setAdminRole(idUser);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping("/{id}/removeAdmin")
    public UserDTO removeAdmin(@PathVariable("id") Long idUser) {
        User user = userService.removeAdminRole(idUser);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping("/{id}/blockUser")
    public UserDTO blockUser(@PathVariable("id") Long idUser) {
        User user = userService.blockUser(idUser);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }

    @PostMapping("/{id}/unblockUser")
    public UserDTO unblockUser(@PathVariable("id") Long idUser) {
        User user = userService.unblockUser(idUser);
        return new UserDTO(user.getFirstName(), user.getLastName(), user.getEmail(), null, user.getImage(), user.isBlocked(), user.getRole(),
                user.getListOfCreatedLots(), user.getListLotOfWinning());
    }
}