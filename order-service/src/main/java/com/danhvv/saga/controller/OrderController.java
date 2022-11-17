package com.danhvv.saga.controller;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.OrderResource;
import com.danhvv.saga.entity.Order;
import com.danhvv.saga.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ConversionService conversionService;

    @PostMapping
    public ResponseEntity<List<OrderResource>> createOrder(@RequestBody List<OrderResource> orderResource) throws JsonProcessingException {
        List<Order> orders = orderResource.stream().map(item -> conversionService.convert(item, Order.class)).collect(Collectors.toList());
        orders = orderService.createOrder(orders);
        return ResponseEntity.ok(orders.stream().map(order -> OrderResource.builder()
                .transactionId(order.getTransactionId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .status(order.getStatus().name())
                .paymentId(order.getPaymentId())
                .build())
                .collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<OrderResource>> getAll(){
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<OrderResource> getOrderDetail(@PathVariable String transactionId){
        Order order = orderService.findByTransactionId(transactionId);
        return ResponseEntity.ok(OrderResource.builder()
                .transactionId(order.getTransactionId())
                .name(order.getName())
                .quantity(order.getQuantity())
                .status(order.getStatus().name())
                .paymentId(order.getPaymentId())
                .build());
    }

}
