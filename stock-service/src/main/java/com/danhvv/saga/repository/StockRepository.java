package com.danhvv.saga.repository;

import com.danhvv.saga.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByName(String name);
}
