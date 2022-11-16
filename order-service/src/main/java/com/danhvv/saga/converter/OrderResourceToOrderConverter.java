package com.danhvv.saga.converter;

import com.danhvv.saga.dto.OrderResource;
import com.danhvv.saga.entity.Order;
import com.danhvv.saga.enums.OrderStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderResourceToOrderConverter implements Converter<OrderResource, Order> {
    @Override
    public Order convert(OrderResource orderResource) {
        return Order.builder()
                .created(LocalDateTime.now())
                .name(orderResource.getName())
                .status(OrderStatus.ORDER_RECEIVED)
                .quantity(orderResource.getQuantity())
                .transactionId(UUID.randomUUID().toString())
                .build();
    }
}
