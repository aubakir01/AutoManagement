package com.example.userlistapp.repository;

import com.example.userlistapp.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    // Последняя запись по валюте
    Optional<ExchangeRate> findTopByCurrencyOrderByFetchedAtDesc(String currency);

    // История по валюте (последние N записей)
    List<ExchangeRate> findTop10ByCurrencyOrderByFetchedAtDesc(String currency);
}