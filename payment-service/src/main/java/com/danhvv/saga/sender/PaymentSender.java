package com.danhvv.saga.sender;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.OrderDto;
import com.danhvv.saga.dto.StockDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void orderNotify(OrderDto orderDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(orderDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(orderDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.ORDER_ROUTING_KEY, message);
    }

    public void stockNotify(StockDto orderDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(orderDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(orderDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.STOCK_ROUTING_KEY, message);
    }
}
