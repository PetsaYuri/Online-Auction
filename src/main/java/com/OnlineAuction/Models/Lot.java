package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.LotDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "lots")
public class Lot {

    public Lot() {}

    public Lot(Long id, LotDTO lotDTO, User creator) {
        this.id = id;
        name = lotDTO.name();
        description = lotDTO.description();
        image = lotDTO.image();
        minimum_price = lotDTO.minimum_price();
        current_price = minimum_price;
        isAvailable = true;
        category = null;
        auction = null;
        this.creator = creator;
        winner = null;
    }

    @Id
    private Long id;

    @Column(nullable = false)
    private String name, description, image;

    @Column(nullable = false)
    private int minimum_price, current_price;

    @Column(nullable = false)
    private boolean isAvailable;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Auction auction;

    @ManyToOne
    private User creator;

    @ManyToOne
    private User winner;

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

    public int getMinimum_price() {
        return minimum_price;
    }

    public void setMinimum_price(int minimum_price) {
        this.minimum_price = minimum_price;
    }

    public int getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(int current_price) {
        this.current_price = current_price;
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
}