package com.example.userlistapp.service;

import com.example.userlistapp.model.ExchangeRate;
import com.example.userlistapp.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final RestTemplate restTemplate;

    // API Нацбанка Казахстана — бесплатный, без ключа
    private static final String NBK_API_URL =
            "https://v1.nationalbank.kz/rss/get_rates.cfm?ftype=xml";

    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.restTemplate = new RestTemplate();
    }

    // Получить курс с API и сохранить в БД
    public ExchangeRate fetchAndSave(String currency) {
        try {
            // Используем exchangerate-api (бесплатный JSON API)
            String url = "https://api.exchangerate-api.com/v4/latest/USD";
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("rates")) {
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");

                // Курс USD к KZT
                Object kztRate = rates.get("KZT");
                if (kztRate != null) {
                    double rate = Double.parseDouble(kztRate.toString());

                    ExchangeRate exchangeRate = new ExchangeRate(
                            "USD/KZT",
                            rate,
                            LocalDateTime.now()
                    );
                    return exchangeRateRepository.save(exchangeRate);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка получения курса: " + e.getMessage());
        }
        return null;
    }

    // Последний сохранённый курс
    public Optional<ExchangeRate> getLatest() {
        return exchangeRateRepository.findTopByCurrencyOrderByFetchedAtDesc("USD/KZT");
    }

    // История последних 10 курсов
    public List<ExchangeRate> getHistory() {
        return exchangeRateRepository.findTop10ByCurrencyOrderByFetchedAtDesc("USD/KZT");
    }
}