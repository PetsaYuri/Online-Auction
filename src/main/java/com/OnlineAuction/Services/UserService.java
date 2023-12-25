package com.OnlineAuction.Services;

import com.OnlineAuction.DTO.UserDTO;
import com.OnlineAuction.Exceptions.User.EmailAlreadyUsesException;
import com.OnlineAuction.Exceptions.User.UserDoesNotHaveAccessException;
import com.OnlineAuction.Models.Bet;
import com.OnlineAuction.Models.Lot;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    private final LotService lotService;

    private final BetService betService;

    private final ImageUploadService imageUploadService;

    @Autowired
    public UserService(UsersRepository usersRepository, LotService lotService, BetService betService, ImageUploadService imageUploadService) {
        this.usersRepository = usersRepository;
        this.lotService = lotService;
        this.betService = betService;
        this.imageUploadService = imageUploadService;
    }

    public List<User> getAll(Pageable pageable) {
        return usersRepository.findAll(pageable).toList();
    }

    public List<User> getByEmail(String email, Pageable pageable) {
        return usersRepository.findByEmailContainsIgnoreCase(email, pageable).toList();
    }

    public List<User> getByFirstNameAndLastName(String firstName, String lastName, Pageable pageable) {
        return usersRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCase(firstName, lastName, pageable).toList();
    }

    public List<User> getByLastName(String lastName, Pageable pageable) {
        return usersRepository.findByLastNameContainsIgnoreCase(lastName, pageable).toList();
    }

    public List<User> getByBlocked(boolean isBlocked, Pageable pageable) {
        return usersRepository.findByIsBlocked(isBlocked, pageable).toList();
    }

    public List<User> getByRole(String role, Pageable pageable) {
        return usersRepository.findByRole(role, pageable).toList();
    }

    public List<User> getByFirstNameAndLastNameAndBlockedAndRole(String firstName, String lastName, boolean isBlocked, String role, Pageable pageable) {
        return usersRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndIsBlockedAndRole(firstName, lastName, isBlocked, role, pageable).toList();
    }

    public List<User> getByLastNameAndBlockedAndRole(String lastName, boolean isBlocked, String role, Pageable pageable) {
        return usersRepository.findByLastNameContainsIgnoreCaseAndIsBlockedAndRole(lastName, isBlocked, role, pageable).toList();
    }

    public List<User> getByFirstNameAndLastNameAndBlocked(String firstName, String lastName, boolean isBlocked, Pageable pageable) {
        return usersRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndIsBlocked(firstName, lastName, isBlocked, pageable).toList();
    }

    public List<User> getByLastNameAndBlocked(String lastName, boolean isBlocked, Pageable pageable) {
        return usersRepository.findByLastNameContainsIgnoreCaseAndIsBlocked(lastName, isBlocked, pageable).toList();
    }

    public List<User> getByFirstNameAndLastNameAndRole(String firstName, String lastName, String role, Pageable pageable) {
        return usersRepository.findByFirstNameContainsIgnoreCaseAndLastNameContainsIgnoreCaseAndRole(firstName, lastName, role, pageable).toList();
    }

    public List<User> getByLastNameAndRole(String lastName, String role, Pageable pageable) {
        return usersRepository.findByLastNameContainsIgnoreCaseAndRole(lastName, role, pageable).toList();
    }

    public List<User> getByBlockedAndRole(boolean isBlocked, String role, Pageable pageable) {
        return usersRepository.findByIsBlockedAndRole(isBlocked, role, pageable).toList();
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

            if (savedUser.getId() == 1) {
                savedUser.setRole("owner");
                usersRepository.save(savedUser);
            }

            return savedUser;
        }
        throw new EmailAlreadyUsesException();
    }

    public User update(UserDTO userDTO, Long id_user) {
        User existUser = usersRepository.getReferenceById(id_user);

        if (userDTO.first_name() != null) {
            existUser.setFirstName(userDTO.first_name());
        }

        if (userDTO.last_name() != null) {
            existUser.setLastName(userDTO.last_name());
        }

        if (userDTO.email() != null) {
            existUser.setEmail(userDTO.email());
        }

        if (userDTO.password() != null) {
            BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
            existUser.setPassword(bCrypt.encode(userDTO.password()));
        }

        return usersRepository.save(existUser);
    }

    public boolean delete(Long id) {
        User user = usersRepository.getReferenceById(id);

        if (!user.getListOfCreatedLots().isEmpty()) {
            List<Lot> listOfCreatedLots = user.getListOfCreatedLots();
            for (int i = 0; i < listOfCreatedLots.size(); i++) {
                lotService.delete(listOfCreatedLots.get(i).getId());
                i = user.getListOfCreatedLots().size() > 0 ? i - 1 : listOfCreatedLots.size();
            }
        }

        if (!user.getListLotOfWinning().isEmpty()) {
            for (Lot lot : user.getListLotOfWinning()) {
                lotService.removeWinnerFromLot(lot);
            }
        }

        if (!user.getBets().isEmpty()) {
            for (Bet bet : user.getBets()) {
                betService.delete(bet.getId());
            }
        }

        usersRepository.delete(user);
        return true;
    }

    public void setBetToUser(User user, Bet bet) {
        List<Bet> betOfPricesList = user.getBets();
        betOfPricesList.add(bet);
        user.setBets(betOfPricesList);
        usersRepository.save(user);
    }

    public void removeBetFromUser(User user) {
        user.setBets(new ArrayList<>());
        usersRepository.save(user);
    }

    public void addLotToListLotsOfWinning(User user, Lot lot) {
        List<Lot> listLotsOfWinning = user.getListLotOfWinning();
        listLotsOfWinning.add(lot);
        user.setListLotOfWinning(listLotsOfWinning);
        usersRepository.save(user);
    }

    public void removeLotFromListLotsOfWinning(Lot lot) {
        User user = lot.getWinner();
        List<Lot> listLotOfWinning = user.getListLotOfWinning();
        listLotOfWinning.remove(lot);
        user.setListLotOfWinning(listLotOfWinning);
        usersRepository.save(user);
    }

    public void removeLotFromListOfCreatedLots(Lot lot) {
        User user = lot.getCreator();
        List<Lot> listOfCreatedLots = user.getListOfCreatedLots();
        listOfCreatedLots.remove(lot);
        user.setListOfCreatedLots(listOfCreatedLots);
        usersRepository.save(user);
    }

    public User setAdminRole(Long idUser) {
        if (calledByAdmin()) {
            User user = getOne(idUser);
            user.setRole("admin");
            return usersRepository.save(user);
        }
        throw new UserDoesNotHaveAccessException();
    }

    public User removeAdminRole(Long idUser) {
        if (calledByAdmin()) {
            User user = getOne(idUser);
            user.setRole("user");
            return usersRepository.save(user);
        }
        throw new UserDoesNotHaveAccessException();
    }

    public boolean calledByAdmin() {
        User user = getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.getRole().equals("admin") || user.getRole().equals("owner");
    }

    public User getUserWhoMadeRequest() {
        return getUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /*public User setImage(Long idUser, MultipartFile file) {
        User user = getOne(idUser);

        if (calledByAdmin() || getUserWhoMadeRequest().equals(user)) {
            String filename = imageUploadService.getUniqueFilename(file.getOriginalFilename() + " ");
            imageUploadService.saveImage(file, filename);
            user.setImage(filename);
            return usersRepository.save(user);
        }   else {
            throw new UserDoesNotHaveAccessException();
        }
    }*/
}