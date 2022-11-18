package com.danhvv.saga.converter;


import com.danhvv.saga.dto.OrderResponse;
import com.danhvv.saga.entity.Order;
import com.danhvv.saga.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OrderResourceToOrderConverter {

    public List<Order> convert(List<OrderResponse> orderResponses) {
        List<Order> orders = new ArrayList<>();
        String transactionId= UUID.randomUUID().toString();
        for(OrderResponse orderResponse : orderResponses){
            orders.add(Order.builder()
                    .created(LocalDateTime.now())
                    .name(orderResponse.getName())
                    .status(OrderStatus.ORDER_RECEIVED)
                    .quantity(orderResponse.getQuantity())
                    .transactionId(transactionId)
                    .build());
        }
        return orders;
    }

}
