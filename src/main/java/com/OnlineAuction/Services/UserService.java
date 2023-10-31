package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Exceptions.Users.EmailAlreadyUsesException;
import com.OnlineAuction.Models.HistoryOfPrice;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> getAll() {
        return usersRepository.findAll();
    }

    public User getOne(Long id) {
        return usersRepository.getReferenceById(id);
    }

    public User getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public User create(UserDTO userDTO) {
        User existUser = usersRepository.findByEmail(userDTO.email());
        if (existUser == null) {
            BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
            String encodedPass = bCrypt.encode(userDTO.password());
            User newUser = new User(userDTO, encodedPass);
            User savedUser = usersRepository.save(newUser);
            return savedUser;
        }
        throw new EmailAlreadyUsesException();
    }

    public User update(UserDTO userDTO, Long id_user) {
        User existUser = usersRepository.getReferenceById(id_user);

        if (userDTO.first_name() != null) {
            existUser.setFirst_name(userDTO.first_name());
        }

        if (userDTO.last_name() != null) {
            existUser.setLast_name(userDTO.last_name());
        }

        if (userDTO.email() != null) {
            existUser.setEmail(userDTO.email());
        }

        if (userDTO.password() != null) {
            BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
            existUser.setPassword(bCrypt.encode(userDTO.password()));
        }

        if (userDTO.image() != null) {
            existUser.setImage(userDTO.image());
        }

        return usersRepository.save(existUser);
    }

    public boolean delete(Long id) {
        User user = usersRepository.getReferenceById(id);
        usersRepository.delete(user);
        return true;
    }

    public void setHistoryOfPrices(User user, HistoryOfPrice historyOfPrice) {
        List<HistoryOfPrice> historyOfPricesList = user.getHistoryOfPrices();
        historyOfPricesList.add(historyOfPrice);
        user.setHistoryOfPrices(historyOfPricesList);
        usersRepository.save(user);
    }

    public void unsetHistoryOfPrices(User user) {
        user.setHistoryOfPrices(new ArrayList<>());
        usersRepository.save(user);
    }
}