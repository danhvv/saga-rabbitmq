package com.danhvv.saga.controller;

import com.danhvv.saga.converter.OrderResourceToOrderConverter;
import com.danhvv.saga.dto.OrderResponse;
import com.danhvv.saga.entity.Order;
import com.danhvv.saga.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderResourceToOrderConverter orderResourceToOrderConverter;

    @PostMapping
    public ResponseEntity<List<OrderResponse>> createOrder(@RequestBody List<OrderResponse> orderResponse) {
        List<Order> orders = orderResourceToOrderConverter.convert(orderResponse);
        orders = orderService.createOrder(orders);
        return ResponseEntity.ok(orders.stream().map(order -> OrderResponse.builder()
                .transactionId(order.getTransactionId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .status(order.getStatus().name())
                .paymentId(order.getPaymentId())
                .build())
                .collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable String transactionId){
        Order order = orderService.findByTransactionId(transactionId);
        return ResponseEntity.ok(OrderResponse.builder()
                .transactionId(order.getTransactionId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .status(order.getStatus().name())
                .paymentId(order.getPaymentId())
                .build());
    }

}
