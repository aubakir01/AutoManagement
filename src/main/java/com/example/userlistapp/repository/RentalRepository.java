package com.example.userlistapp.repository;

import com.example.userlistapp.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("""
        select r from Rental r
        where
          (:q is null or :q = '' or
           lower(r.car.brand) like lower(concat('%', :q, '%')) or
           lower(r.car.model) like lower(concat('%', :q, '%')))
        and
          (:fromDate is null or r.endDate >= :fromDate)
        and 
          (:toDate is null or r.startDate <= :toDate)
        order by r.id desc
    """)
    List<Rental> search(@Param("q") String q,
                        @Param("fromDate") LocalDate fromDate,
                        @Param("toDate") LocalDate toDate);
}