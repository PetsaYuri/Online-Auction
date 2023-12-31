package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.LotDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "lots")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lot {

    public Lot() {}

    public Lot(Long id, LotDTO lotDTO, User creator) {
        this.id = id;
        name = lotDTO.name();
        description = lotDTO.description();
        image = lotDTO.image();
        minimumPrice = lotDTO.minimum_price();
        currentPrice = minimumPrice;
        isAvailable = false;
        category = lotDTO.category();
        auction = null;
        this.creator = creator;
        winner = null;
    }

    @Id
    private Long id;

    @Column(nullable = false)
    private String name, description, image;

    @Column(name = "minimum_price",nullable = false)
    private int minimumPrice;

    @Column(name = "current_price",nullable = false)
    private int currentPrice;

    @Column(nullable = false)
    private boolean isAvailable;

    @ManyToOne(optional = false)
    private Category category;

    @ManyToOne
    @JsonIgnore
    private Auction auction;

    @ManyToOne(optional = false)
    private User creator;

    @ManyToOne
    private User winner;

    @OneToMany
    private List<Bet> bets;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(int minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }
}