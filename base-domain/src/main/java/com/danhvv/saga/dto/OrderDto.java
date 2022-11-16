package com.danhvv.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDto {
    private String transactionId;
    private String name;
    private String status;
    private Double price;
    private String paymentId;
    private int quantity;
}
