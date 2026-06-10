package com.example.userlistapp.repository;

import com.example.userlistapp.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(String status);

    List<Car> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(String brand, String model);

    List<Car> findByStatusAndBrandContainingIgnoreCaseOrStatusAndModelContainingIgnoreCase(
            String status1, String brand,
            String status2, String model
    );

    @Query("""
        select c from Car c
        where
          (:q is null or :q = '' or
           lower(c.brand) like lower(concat('%', :q, '%')) or
           lower(c.model) like lower(concat('%', :q, '%')))
        and
          (:status is null or :status = '' or c.status = :status)
        and
          (:yearFrom is null or c.year >= :yearFrom)
        and
          (:yearTo is null or c.year <= :yearTo)
        and
          (:priceFrom is null or c.pricePerDay >= :priceFrom)
        and
          (:priceTo is null or c.pricePerDay <= :priceTo)
        order by c.id asc
    """)
    List<Car> search(
            @Param("q") String q,
            @Param("status") String status,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            @Param("priceFrom") Integer priceFrom,
            @Param("priceTo") Integer priceTo
    );
}