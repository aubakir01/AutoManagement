package com.example.userlistapp.controller;

import java.time.LocalDate;
import com.example.userlistapp.model.Car;
import com.example.userlistapp.model.Rental;
import com.example.userlistapp.repository.CarRepository;
import com.example.userlistapp.repository.RentalRepository;
import com.example.userlistapp.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RentalController {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public RentalController(RentalRepository rentalRepository,
                            CarRepository carRepository,
                            UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/rentals")
    public String rentals(@RequestParam(required = false) String q,
                          @RequestParam(required = false) String from,
                          @RequestParam(required = false) String to,
                          Model model) {

        LocalDate fromDate = null;
        LocalDate toDate = null;

        try {
            if (from != null && !from.isBlank()) fromDate = LocalDate.parse(from);
            if (to != null && !to.isBlank()) toDate = LocalDate.parse(to);
        } catch (Exception e) {
            model.addAttribute("error", "Неверный формат дат.");
        }

        model.addAttribute("rentals", rentalRepository.search(q, fromDate, toDate));
        model.addAttribute("q", q);
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("pageTitle", "Аренды");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Раздел: Аренды");

        return "rentals";
    }

    @GetMapping("/rentals/new")
    public String newRentalForm(Model model) {
        model.addAttribute("cars", carRepository.findByStatus("Свободен"));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("pageTitle", "Новая аренда");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Создание аренды");
        return "rental_form";
    }

    @PostMapping("/rentals")
    public String createRental(@RequestParam(required = false) Long carId,
                               @RequestParam(required = false) Long userId,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               Model model) {

        model.addAttribute("cars", carRepository.findByStatus("Свободен"));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("pageTitle", "Новая аренда");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Создание аренды");

        if (carId == null || userId == null ||
                startDate == null || startDate.isBlank() ||
                endDate == null || endDate.isBlank()) {
            model.addAttribute("error", "Заполните все поля.");
            return "rental_form";
        }

        LocalDate sd, ed;
        try {
            sd = LocalDate.parse(startDate);
            ed = LocalDate.parse(endDate);
        } catch (Exception e) {
            model.addAttribute("error", "Неверный формат даты.");
            return "rental_form";
        }

        if (ed.isBefore(sd)) {
            model.addAttribute("error", "Дата окончания не может быть раньше даты начала.");
            return "rental_form";
        }

        var car = carRepository.findById(carId).orElse(null);
        var user = userRepository.findById(userId).orElse(null);

        if (car == null || user == null) {
            model.addAttribute("error", "Выберите корректные значения.");
            return "rental_form";
        }

        if (!"Свободен".equalsIgnoreCase(car.getStatus())) {
            model.addAttribute("error", "Эта машина уже арендована или недоступна.");
            return "rental_form";
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(sd, ed) + 1;
        int total = (int) (days * car.getPricePerDay());

        Rental rental = new Rental();
        rental.setCar(car);
        rental.setUser(user);
        rental.setStartDate(sd);
        rental.setEndDate(ed);
        rental.setTotalPrice(total);
        rentalRepository.save(rental);

        car.setStatus("Арендован");
        carRepository.save(car);

        return "redirect:/rentals";
    }

    @PostMapping("/rentals/complete/{id}")
    public String completeRental(@PathVariable Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental id: " + id));

        Car car = rental.getCar();
        car.setStatus("Свободен");
        carRepository.save(car);

        return "redirect:/rentals";
    }
}