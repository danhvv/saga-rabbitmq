package com.danhvv.saga.service;

import com.danhvv.saga.dto.OrderResponse;
import com.danhvv.saga.entity.Order;
import com.danhvv.saga.enums.OrderStatus;
import com.danhvv.saga.event.CreateOrdersEvent;
import com.danhvv.saga.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ModelMapper modelMapper;

    @Transactional
    public List<Order> createOrder(List<Order> orders) {
        List<Order> lOrders = orderRepository.saveAll(orders);
        applicationEventPublisher.publishEvent(new CreateOrdersEvent(lOrders));
        return lOrders;
    }

    public void updateOrder(String transactionId, OrderStatus status, String paymentId) {
        Optional<Order> lOrder = orderRepository.findByTransactionId(transactionId);
        if (lOrder.isPresent()) {
            Order order = lOrder.get();
            order.setStatus(status);
            order.setPaymentId(paymentId);
            orderRepository.save(order);
        }
    }

    public Order findByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Order can not be found by transactionId : %s", transactionId)));
    }

    public List<OrderResponse> getAll() {
        return modelMapper.map(orderRepository.findAll(), new TypeToken<List<OrderResponse>>() {
        }.getType());
    }

}
