package com.danhvv.saga.service;

import com.danhvv.saga.dto.PaymentDto;
import com.danhvv.saga.dto.PaymentResponse;
import com.danhvv.saga.dto.StockDto;
import com.danhvv.saga.entity.Payment;
import com.danhvv.saga.enums.OrderStatus;
import com.danhvv.saga.enums.PaymentStatus;
import com.danhvv.saga.enums.StockStatus;
import com.danhvv.saga.repository.PaymentRepository;
import com.danhvv.saga.sender.PaymentSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentSender paymentSender;
    private final ModelMapper modelMapper;

    public List<PaymentResponse> getAll() {
        return modelMapper.map(paymentRepository.findAll(), new TypeToken<List<PaymentResponse>>() {
        }.getType());
    }

    public Payment findByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Payment can not be found by transactionId : %s", transactionId)));
    }

    @Transactional
    public Payment createPayment(PaymentDto paymentDto) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.PAYMENT_PENDING)
                .totalPrice(paymentDto.getOrders().stream().mapToDouble(value -> value.getQuantity() * value.getPrice()).sum())
                .transactionId(paymentDto.getTransactionId())
                .build();
        return paymentRepository.saveAndFlush(payment);
    }

    public void updateStatus(String transactionId, PaymentStatus paymentStatus) {
        Optional<Payment> lPayment = paymentRepository.findByTransactionId(transactionId);
        Payment payment = lPayment.orElseThrow(() -> new IllegalArgumentException("Payment data can not be found in database"));
        payment.setStatus(paymentStatus);
        paymentRepository.save(payment);
    }

    @Transactional
    public void buyFrom3rdPartyApp(PaymentDto paymentDto) {
        Optional<Payment> payment = paymentRepository.findByTransactionId(paymentDto.getTransactionId());

        if (payment.isPresent()) {
            Payment lPayment = payment.get();
            boolean isSuccess = new Random().nextBoolean();
            if (!isSuccess) {
                lPayment.setStatus(PaymentStatus.PAYMENT_FAILED);
                paymentRepository.save(lPayment);
                sendStockFailedNotification(paymentDto);
                sendOrderFailedNotification(paymentDto);
            } else {
                sendNotificationToOrderService(paymentDto, lPayment);
            }
        } else {
            sendStockFailedNotification(paymentDto);
            sendOrderFailedNotification(paymentDto);
        }
    }

    private void sendStockFailedNotification(PaymentDto paymentDto) {
        try {
            paymentSender.stockNotify(StockDto.builder()
                    .transactionId(paymentDto.getTransactionId())
                    .orders(paymentDto.getOrders())
                    .status(StockStatus.STOCK_FAILED.name())
                    .build());
        } catch (JsonProcessingException e) {
        }
    }

    private void sendOrderFailedNotification(PaymentDto paymentDto) {
        paymentDto.getOrders().forEach(orderDto -> {
            try {
                orderDto.setStatus(OrderStatus.ORDER_FAILED.name());
                paymentSender.orderNotify(orderDto);
            } catch (JsonProcessingException e) {
            }
        });
    }

    private void sendNotificationToOrderService(PaymentDto paymentDto, Payment lPayment) {
        lPayment.setStatus(PaymentStatus.PAYMENT_COMPLETED);
        final String paymentId = lPayment.getId();
        paymentRepository.save(lPayment);
        paymentDto.getOrders().forEach(orderDto -> {
            try {
                orderDto.setPaymentId(paymentId);
                orderDto.setStatus(OrderStatus.ORDER_COMPLETED.name());
                paymentSender.orderNotify(orderDto);
            } catch (JsonProcessingException e) {
            }
        });
    }

    public PaymentResponse findById(String paymentId) {
        return modelMapper.map(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment can not be found by given id")), PaymentResponse.class);
    }
}
