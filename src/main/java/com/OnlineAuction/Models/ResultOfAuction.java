package com.OnlineAuction.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "results_of_auctions")
public class ResultOfAuction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameAuction;

    @Column(nullable = false)
    private Timestamp start, ends;

    @OneToOne
    private Auction auction;
}
