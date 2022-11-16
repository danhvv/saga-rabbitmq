package com.danhvv.saga.controller;

import com.danhvv.saga.dto.StockResource;
import com.danhvv.saga.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockRepository stockRepository;

    @GetMapping
    public ResponseEntity<List<StockResource>> getStocks() {
        return ResponseEntity.ok(stockRepository.findAll().stream()
                .map(stock -> StockResource.builder()
                        .name(stock.getName())
                        .quantity(stock.getQuantity())
                        .build())
                .collect(Collectors.toList()));
    }

}

