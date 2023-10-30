package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.AuctionDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Table(name = "auctions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Auction {

    public Auction() {}

    public Auction(Long id, AuctionDTO auctionDTO) {
        this.id = id;
        title = auctionDTO.title();
        description = auctionDTO.description();
        Calendar calendar = Calendar.getInstance();
        start = Timestamp.from(calendar.toInstant());
        calendar.add(Calendar.DATE, auctionDTO.number_days());
        ends = Timestamp.from(calendar.toInstant());
        lots = auctionDTO.lots();
    }

    public Auction(Long id, String description, List<Lot> lots, int duration) {
        this.id = id;
        title = "Auction #" + id;
        this.description = description;
        Calendar calendar = Calendar.getInstance();
        start = Timestamp.from(calendar.toInstant());
        calendar.add(Calendar.DATE, duration);
        ends = Timestamp.from(calendar.toInstant());
        this.lots = lots;
    }

    @Id
    private Long id;

    @Column(nullable = false)
    private String title, description;

    @Column(nullable = false)
    private Timestamp start, ends;

    @OneToMany
    private List<Lot> lots;

    @OneToOne
    private ResultOfAuction resultOfAuction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnds() {
        return ends;
    }

    public void setEnds(Timestamp ends) {
        this.ends = ends;
    }

    public List<Lot> getLots() {
        return lots;
    }

    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    public ResultOfAuction getResultOfAuction() {
        return resultOfAuction;
    }

    public void setResultOfAuction(ResultOfAuction resultOfAuction) {
        this.resultOfAuction = resultOfAuction;
    }
}