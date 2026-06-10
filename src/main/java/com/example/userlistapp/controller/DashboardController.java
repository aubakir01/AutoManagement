package com.example.userlistapp.controller;

import com.example.userlistapp.model.ExchangeRate;
import com.example.userlistapp.repository.CarRepository;
import com.example.userlistapp.repository.RentalRepository;
import com.example.userlistapp.repository.UserRepository;
import com.example.userlistapp.service.ExchangeRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {

    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final ExchangeRateService exchangeRateService;

    public DashboardController(UserRepository userRepository,
                               CarRepository carRepository,
                               RentalRepository rentalRepository,
                               ExchangeRateService exchangeRateService) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.rentalRepository = rentalRepository;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        long usersCount = userRepository.count();
        long carsCount = carRepository.count();
        long rentalsCount = rentalRepository.count();
        long freeCars = carRepository.findAll().stream()
                .filter(c -> "Свободен".equalsIgnoreCase(c.getStatus()))
                .count();

        // Курс доллара
        Optional<ExchangeRate> rate = exchangeRateService.getLatest();
        rate.ifPresent(r -> model.addAttribute("usdRate", r));

        model.addAttribute("usersCount", usersCount);
        model.addAttribute("carsCount", carsCount);
        model.addAttribute("rentalsCount", rentalsCount);
        model.addAttribute("freeCars", freeCars);
        model.addAttribute("pageTitle", "Главная");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Панель управления");

        return "dashboard";
    }
}