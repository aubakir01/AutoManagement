package com.example.userlistapp.api;

import com.example.userlistapp.model.Car;
import com.example.userlistapp.model.Rental;
import com.example.userlistapp.repository.CarRepository;
import com.example.userlistapp.repository.RentalRepository;
import com.example.userlistapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rentals")
public class RentalApiController {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public RentalApiController(RentalRepository rentalRepository,
                               CarRepository carRepository,
                               UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    // GET /api/rentals — все аренды
    @GetMapping
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    // GET /api/rentals/{id} — аренда по ID
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return rentalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/rentals/filter?q=Toyota&from=2024-01-01&to=2024-12-31
    @GetMapping("/filter")
    public List<Rental> getRentalsByFilter(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        LocalDate fromDate = null;
        LocalDate toDate = null;
        try {
            if (from != null && !from.isBlank()) fromDate = LocalDate.parse(from);
            if (to != null && !to.isBlank()) toDate = LocalDate.parse(to);
        } catch (Exception e) {
            return List.of();
        }
        return rentalRepository.search(q, fromDate, toDate);
    }

    // POST /api/rentals — создать аренду
    @PostMapping
    public ResponseEntity<?> createRental(@RequestBody Map<String, String> body) {
        try {
            Long carId = Long.parseLong(body.get("carId"));
            Long userId = Long.parseLong(body.get("userId"));
            LocalDate startDate = LocalDate.parse(body.get("startDate"));
            LocalDate endDate = LocalDate.parse(body.get("endDate"));

            var car = carRepository.findById(carId).orElse(null);
            var user = userRepository.findById(userId).orElse(null);

            if (car == null || user == null)
                return ResponseEntity.badRequest().body("Машина или пользователь не найдены");

            if (!"Свободен".equalsIgnoreCase(car.getStatus()))
                return ResponseEntity.badRequest().body("Машина недоступна");

            if (endDate.isBefore(startDate))
                return ResponseEntity.badRequest().body("Неверные даты");

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            int total = (int) (days * car.getPricePerDay());

            Rental rental = new Rental();
            rental.setCar(car);
            rental.setUser(user);
            rental.setStartDate(startDate);
            rental.setEndDate(endDate);
            rental.setTotalPrice(total);
            rentalRepository.save(rental);

            car.setStatus("Арендован");
            carRepository.save(car);

            return ResponseEntity.status(201).body(rental);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    // PATCH /api/rentals/{id}/complete — завершить аренду
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeRental(@PathVariable Long id) {
        return rentalRepository.findById(id).map(rental -> {
            Car car = rental.getCar();
            car.setStatus("Свободен");
            carRepository.save(car);
            return ResponseEntity.ok(Map.of("message", "Аренда завершена", "carId", car.getId()));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/rentals/{id} — удалить аренду
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable Long id) {
        if (!rentalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rentalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}