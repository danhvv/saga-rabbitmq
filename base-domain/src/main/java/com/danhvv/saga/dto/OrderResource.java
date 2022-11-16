package com.danhvv.saga.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderResource {
    private String transactionId;
    private String name;
    private Integer quantity;
    private String status;
    private String paymentId;
}
