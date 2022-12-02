package com.danhvv.saga.controller;

import com.danhvv.saga.dto.OrderResponse;
import com.danhvv.saga.dto.PaymentResponse;
import com.danhvv.saga.entity.Payment;
import com.danhvv.saga.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAll(){
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.findById(paymentId));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentDetail(@PathVariable String transactionId){
        Payment payment = paymentService.findByTransactionId(transactionId);
        return ResponseEntity.ok(PaymentResponse.builder()
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus().name())
                .totalPrice(payment.getTotalPrice())
                .build());
    }

}

