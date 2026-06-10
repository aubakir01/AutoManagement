package com.example.userlistapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String currency; // USD, EUR и т.д.
    private Double rate;     // курс к тенге
    private LocalDateTime fetchedAt; // когда получили

    public ExchangeRate() {}

    public ExchangeRate(String currency, Double rate, LocalDateTime fetchedAt) {
        this.currency = currency;
        this.rate = rate;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() { return id; }
    public String getCurrency() { return currency; }
    public Double getRate() { return rate; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }

    public void setId(Long id) { this.id = id; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setRate(Double rate) { this.rate = rate; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}