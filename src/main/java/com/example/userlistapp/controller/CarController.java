package com.example.userlistapp.controller;

import com.example.userlistapp.model.Car;
import com.example.userlistapp.repository.CarRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
public class CarController {

    private final CarRepository carRepository;

    // Папка куда сохраняем фото (создаётся автоматически)
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/cars/";

    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    private List<Car> getFiltered(String q, String status,
                                  Integer yearFrom, Integer yearTo,
                                  Integer priceFrom, Integer priceTo) {
        return carRepository.search(q, status, yearFrom, yearTo, priceFrom, priceTo);
    }

    @GetMapping("/cars")
    public String cars(@RequestParam(required = false) String status,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer yearFrom,
                       @RequestParam(required = false) Integer yearTo,
                       @RequestParam(required = false) Integer priceFrom,
                       @RequestParam(required = false) Integer priceTo,
                       Model model) {

        model.addAttribute("cars", getFiltered(q, status, yearFrom, yearTo, priceFrom, priceTo));
        model.addAttribute("status", status);
        model.addAttribute("q", q);
        model.addAttribute("yearFrom", yearFrom);
        model.addAttribute("yearTo", yearTo);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);
        model.addAttribute("pageTitle", "Автомобили");
        model.addAttribute("headerTitle", "Сервис аренды автомобилей");
        model.addAttribute("headerSubtitle", "Раздел: Автомобили");

        return "cars";
    }

    @GetMapping("/cars/export")
    public void exportToExcel(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer priceFrom,
            @RequestParam(required = false) Integer priceTo,
            HttpServletResponse response) throws IOException {

        List<Car> cars = getFiltered(q, status, yearFrom, yearTo, priceFrom, priceTo);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=cars.xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Автомобили");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row header = sheet.createRow(0);
            String[] columns = {"ID", "Марка", "Модель", "Год", "Цена за день", "Статус"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Car car : cars) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(car.getId());
                row.createCell(1).setCellValue(car.getBrand());
                row.createCell(2).setCellValue(car.getModel());
                row.createCell(3).setCellValue(car.getYear());
                row.createCell(4).setCellValue(car.getPricePerDay());
                row.createCell(5).setCellValue(car.getStatus());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/cars/new")
    public String newCarForm(Model model) {
        model.addAttribute("car", new Car());
        return "car_form";
    }

    @PostMapping("/cars")
    public String createCar(@Valid @ModelAttribute Car car,
                            BindingResult bindingResult,
                            @RequestParam(value = "photo", required = false) MultipartFile photo,
                            Model model) throws IOException {

        if (bindingResult.hasErrors()) return "car_form";

        savePhoto(photo, car, null);
        carRepository.save(car);
        return "redirect:/cars";
    }

    @GetMapping("/cars/edit/{id}")
    public String editCarForm(@PathVariable Long id, Model model) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid car id: " + id));
        model.addAttribute("car", car);
        return "car_form";
    }

    @PostMapping("/cars/update/{id}")
    public String updateCar(@PathVariable Long id,
                            @Valid @ModelAttribute Car car,
                            BindingResult bindingResult,
                            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {

        if (bindingResult.hasErrors()) {
            car.setId(id);
            return "car_form";
        }

        // Берём старое фото если новое не загружено
        Car existing = carRepository.findById(id).orElse(null);
        String oldPhoto = existing != null ? existing.getPhotoPath() : null;

        car.setId(id);
        savePhoto(photo, car, oldPhoto);
        carRepository.save(car);
        return "redirect:/cars";
    }

    // Вспомогательный метод сохранения файла
    private void savePhoto(MultipartFile photo, Car car, String oldPhoto) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            // Создаём папку если не существует
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Уникальное имя файла
            String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, photo.getBytes());

            // Сохраняем путь для доступа через браузер
            car.setPhotoPath("/uploads/cars/" + filename);
        } else {
            car.setPhotoPath(oldPhoto);
        }
    }

    @PostMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        carRepository.deleteById(id);
        return "redirect:/cars";
    }
}