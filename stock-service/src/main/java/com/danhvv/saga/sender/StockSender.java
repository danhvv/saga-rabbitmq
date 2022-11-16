package com.danhvv.saga.sender;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.OrderDto;
import com.danhvv.saga.dto.PaymentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StockSender {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void orderNotify(OrderDto orderDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(orderDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(orderDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.ORDER_ROUTING_KEY, message);
    }

    public void paymentNotify(PaymentDto paymentDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(paymentDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(paymentDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.PAYMENT_ROUTING_KEY, message);
    }
}
