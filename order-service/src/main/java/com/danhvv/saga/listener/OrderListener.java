package com.danhvv.saga.listener;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.OrderDto;
import com.danhvv.saga.enums.OrderStatus;
import com.danhvv.saga.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class OrderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderListener.class);

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ApplicationConstant.ORDER_QUEUE, durable = "true"),
            exchange = @Exchange(value = ApplicationConstant.EXCHANGE, ignoreDeclarationExceptions = "true"),
            key = ApplicationConstant.ORDER_ROUTING_KEY)
    )
    public void listen(Message message) throws IOException {
        OrderDto orderDto = objectMapper.readValue(message.getBody(), OrderDto.class);
        if (orderDto.getStatus() != null && !orderDto.getStatus().trim().equals("")) {
            OrderStatus orderStatus = OrderStatus.valueOf(orderDto.getStatus());
            LOGGER.info("Order {} request is received. Transaction Id: {}", orderStatus.name(), orderDto.getTransactionId());
            switch (orderStatus) {
                case ORDER_PENDING:
                case ORDER_COMPLETED:
                case ORDER_FAILED:
                case ORDER_STOCK_COMPLETED:
                    orderService.updateOrder(orderDto.getTransactionId(), orderStatus, orderDto.getPaymentId());
                    break;
                default:
                    break;
            }

        } else {
            throw new IllegalArgumentException(String.format("Order Status is wrong. %s", orderDto.getStatus()));
        }
    }
}
