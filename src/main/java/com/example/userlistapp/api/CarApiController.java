package com.example.userlistapp.api;

import com.example.userlistapp.model.Car;
import com.example.userlistapp.repository.CarRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cars")
public class CarApiController {

    private final CarRepository carRepository;

    public CarApiController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // GET /api/cars — все машины
    @GetMapping
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // GET /api/cars/{id} — машина по ID
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return carRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/cars/filter?status=Свободен&q=Toyota
    @GetMapping("/filter")
    public List<Car> getCarsByFilter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q) {

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasQ = q != null && !q.isBlank();

        if (hasStatus && hasQ) {
            return carRepository
                    .findByStatusAndBrandContainingIgnoreCaseOrStatusAndModelContainingIgnoreCase(
                            status, q, status, q);
        } else if (hasStatus) {
            return carRepository.findByStatus(status);
        } else if (hasQ) {
            return carRepository
                    .findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(q, q);
        } else {
            return carRepository.findAll();
        }
    }

    // POST /api/cars — создать машину
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car saved = carRepository.save(car);
        return ResponseEntity.status(201).body(saved);
    }

    // PUT /api/cars/{id} — полное обновление машины
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id,
                                         @RequestBody Car car) {
        if (!carRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        car.setId(id);
        return ResponseEntity.ok(carRepository.save(car));
    }

    // PATCH /api/cars/{id}/status — обновить только статус
    @PatchMapping("/{id}/status")
    public ResponseEntity<Car> updateCarStatus(@PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        return carRepository.findById(id).map(car -> {
            String newStatus = body.get("status");
            if (newStatus != null && !newStatus.isBlank()) {
                car.setStatus(newStatus);
                carRepository.save(car);
            }
            return ResponseEntity.ok(car);
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/cars/{id} — удалить машину
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        if (!carRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}