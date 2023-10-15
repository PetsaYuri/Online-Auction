package com.OnlineAuction.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "auctions")
public class Auction {

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
}
