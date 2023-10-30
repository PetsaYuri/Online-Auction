package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.HistoryOfPriceDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Table(name = "histories_of_prices")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HistoryOfPrice {

    public HistoryOfPrice() {}

    public HistoryOfPrice(HistoryOfPriceDTO historyDTO, User user, Lot lot) {
        full_name = user.getFirst_name() + " " + user.getLast_name();
        name_lot = lot.getName();
        price = historyDTO.price();
        date = Timestamp.from(Calendar.getInstance().toInstant());
        this.user = user;
        this.lot = lot;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String full_name, name_lot;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private Timestamp date;

    @ManyToOne
    private User user;

    @OneToOne
    private Lot lot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getName_lot() {
        return name_lot;
    }

    public void setName_lot(String name_lot) {
        this.name_lot = name_lot;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }
}