package com.OnlineAuction.Models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "histories_of_prices")
public class HistoryOfPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String full_name, name_lot;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private Timestamp date;

    @OneToOne
    private User user;

    @OneToOne
    private Lot lot;
}
