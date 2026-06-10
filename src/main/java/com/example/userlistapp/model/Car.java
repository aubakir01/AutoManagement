package com.example.userlistapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Марка обязательна")
    private String brand;

    @NotBlank(message = "Модель обязательна")
    private String model;

    @NotNull(message = "Год обязателен")
    @Min(value = 1950, message = "Год должен быть >= 1950")
    private Integer year;

    @NotNull(message = "Цена за день обязательна")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    private Integer pricePerDay;

    @NotBlank(message = "Статус обязателен")
    private String status;

    private String photoPath; // путь к фото

    public Car() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Integer pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}