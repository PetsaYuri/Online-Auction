package com.OnlineAuction.Models;

import com.OnlineAuction.DTO.ResultOfActionDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "results_of_auctions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ResultOfAuction {

    public ResultOfAuction() {}

    public ResultOfAuction(ResultOfActionDTO resultOfActionDTO) {
        auction = resultOfActionDTO.auction();
        nameAuction = auction.getTitle();
        start = auction.getStart();
        ends = auction.getEnds();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameAuction;

    @Column(nullable = false)
    private Timestamp start, ends;

    @OneToOne(optional = false)
    private Auction auction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameAuction() {
        return nameAuction;
    }

    public void setNameAuction(String nameAuction) {
        this.nameAuction = nameAuction;
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

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}