package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Exceptions.User.EmailAlreadyUsesException;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    private final LotService lotService;
    @Autowired
    public UserService(UsersRepository usersRepository, LotService lotService) {
        this.usersRepository = usersRepository;
        this.lotService = lotService;
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
            return usersRepository.save(newUser);
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
        if (!usersRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to find User with id " + id);
        }

        User user = usersRepository.getReferenceById(id);
        usersRepository.delete(user);
        return true;
    }

    public void addBet(User user, Bet bet) {
        List<Bet> betOfPricesList = user.getBets();
        betOfPricesList.add(bet);
        user.setBets(betOfPricesList);
        usersRepository.save(user);
    }

    public void deleteBet(User user) {
        user.setBets(new ArrayList<>());
        usersRepository.save(user);
    }

    public void addLotToListLotsOfWinning(User user, Lot lot) {
        List<Lot> listLotsOfWinning = user.getListLotOfWinning();
        listLotsOfWinning.add(lot);
        user.setListLotOfWinning(listLotsOfWinning);
        usersRepository.save(user);
    }

    public void removeLotFromListLotsOfWinning(User user, Lot lot) {
        List<Lot> listLotOfWinning = user.getListLotOfWinning();
        listLotOfWinning.remove(lot);
        user.setListLotOfWinning(listLotOfWinning);
        usersRepository.save(user);
    }
}