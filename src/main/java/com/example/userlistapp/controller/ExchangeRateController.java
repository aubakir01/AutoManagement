package com.example.userlistapp.controller;

import com.example.userlistapp.model.ExchangeRate;
import com.example.userlistapp.service.ExchangeRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange")
    public String exchangePage(Model model) {
        Optional<ExchangeRate> latest = exchangeRateService.getLatest();
        latest.ifPresent(r -> model.addAttribute("latest", r));
        model.addAttribute("history", exchangeRateService.getHistory());
        model.addAttribute("pageTitle", "Курс валют");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Курс доллара");
        return "exchange";
    }


    @PostMapping("/exchange/fetch")
    public String fetchRate() {
        exchangeRateService.fetchAndSave("USD/KZT");
        return "redirect:/exchange";
    }
}