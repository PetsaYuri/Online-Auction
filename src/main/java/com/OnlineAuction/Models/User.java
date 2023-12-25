package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    public User() {}

    public User(UserDTO userDTO, String encodedPass) {
        firstName = userDTO.first_name();
        lastName = userDTO.last_name();
        email = userDTO.email();
        password = encodedPass;
        notifications = new ArrayList<>();
        isBlocked = false;
        listOfCreatedLots = new ArrayList<>();
        listLotOfWinning = new ArrayList<>();
        role = "user";
        image = userDTO.image();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email, password, role;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column
    private String image;

    @Column(nullable = false)
    private List<String> notifications;

    @Column(nullable = false)
    private boolean isBlocked;

    @OneToMany(mappedBy = "creator")
    private List<Lot> listOfCreatedLots;

    @OneToMany(mappedBy = "winner", fetch = FetchType.EAGER)
    private List<Lot> listLotOfWinning;

    @OneToMany
    private List<Bet> bets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public List<Lot> getListOfCreatedLots() {
        return listOfCreatedLots;
    }

    public void setListOfCreatedLots(List<Lot> listOfCreatedLots) {
        this.listOfCreatedLots = listOfCreatedLots;
    }

    public List<Lot> getListLotOfWinning() {
        return listLotOfWinning;
    }

    public void setListLotOfWinning(List<Lot> listLotOfWinning) {
        this.listLotOfWinning = listLotOfWinning;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }
}