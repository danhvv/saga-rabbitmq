package com.danhvv.saga.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class OrderResponse {
    @Schema(hidden = true)
    private String transactionId;
    private String name;
    private Integer quantity;
    @Schema(hidden = true)
    private String status;
    @Schema(hidden = true)
    private String paymentId;
}
