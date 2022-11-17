package com.danhvv.saga.service;

import com.danhvv.saga.dto.OrderDto;
import com.danhvv.saga.dto.PaymentDto;
import com.danhvv.saga.dto.StockDto;
import com.danhvv.saga.entity.Stock;
import com.danhvv.saga.enums.OrderStatus;
import com.danhvv.saga.enums.PaymentStatus;
import com.danhvv.saga.repository.StockRepository;
import com.danhvv.saga.sender.StockSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class StockService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final StockSender stockSender;
    private static final List<String> AVAILABLE_PRODUCTS = Arrays.asList("product1", "product2", "product3", "product4", "product5");

    @PostConstruct
    public void init() {
        stockRepository.deleteAll();
        AVAILABLE_PRODUCTS.forEach(product -> {
            int totalQuantity = new Random().nextInt(20);
            stockRepository.save(Stock.builder()
                    .name(product)
                    .quantity(totalQuantity)
                    .build());
        });
    }


    @Transactional
    public void prepareStock(StockDto stockDto) {
        for (OrderDto orderDto : stockDto.getOrders()) {
            Optional<Stock> lStock = stockRepository.findByName(orderDto.getName());
            if (lStock.isPresent()) {
                Stock stock = lStock.get();
                int total = stock.getQuantity();
                int requestedCount = orderDto.getQuantity();
                if (total - requestedCount >= 0) {
                    stock.setQuantity(total - requestedCount);
                    stockRepository.save(stock);
                    orderDto.setStatus(OrderStatus.ORDER_STOCK_COMPLETED.name());
                    try {
                        LOGGER.info("Sending ORDER_STOCK_COMPLETED notification to order queue. TransactionId: {}",
                                stockDto.getTransactionId());
                        stockSender.orderNotify(orderDto);
                    } catch (JsonProcessingException e) {
                        // nothing to do
                    }
                } else {
                    sendPaymentFailedNotification(stockDto);
                    recalculateStockValues(stockDto.getOrders());
                    sendOrderFailedNotification(stockDto.getOrders());
                    return;
                }
            } else {
                sendOrderFailedNotification(stockDto.getOrders());
                sendPaymentFailedNotification(stockDto);
                return;
            }
        }
        sendPaymentNotification(stockDto, PaymentStatus.PAYMENT_AVAILABLE);
    }

    public void recalculateStockValues(List<OrderDto> orders) {
        orders.forEach(orderDto -> {
                    Optional<Stock> lStock = stockRepository.findByName(orderDto.getName());
                    lStock.ifPresent(stock -> {
                        int quantity = orderDto.getQuantity();
                        stock.setQuantity(stock.getQuantity() + quantity);
                        stockRepository.save(stock);
                    });
                });
    }

    private void sendPaymentNotification(StockDto stockDto, PaymentStatus status) {
        LOGGER.info("Sending {} notification to payment queue. TransactionId: {}", status.name(), stockDto.getTransactionId());
        try {
            stockSender.paymentNotify(PaymentDto.builder()
                    .transactionId(stockDto.getTransactionId())
                    .orders(stockDto.getOrders())
                    .status(status.name())
                    .build());
        } catch (JsonProcessingException e) {
            // nothing to do
        }
    }

    private void sendPaymentFailedNotification(StockDto stockDto) {
        sendPaymentNotification(stockDto, PaymentStatus.PAYMENT_FAILED);
    }

    private void sendOrderFailedNotification(List<OrderDto> orderDtos) {
        orderDtos.forEach(item -> {
            item.setStatus(OrderStatus.ORDER_FAILED.name());
            try {
                stockSender.orderNotify(item);
            } catch (JsonProcessingException e) {
                //Nothing to do for now
            }
        });
    }
}