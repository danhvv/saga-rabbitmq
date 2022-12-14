package com.danhvv.saga.sender;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.PaymentDto;
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
public class OrderSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void paymentNotify(PaymentDto paymentDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(paymentDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(paymentDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.PAYMENT_ROUTING_KEY, message);
    }

    public void stockNotify(StockDto stockDto) throws JsonProcessingException {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(stockDto.getTransactionId());
        Message message = new Message(objectMapper.writeValueAsBytes(stockDto), messageProperties);
        rabbitTemplate.send(ApplicationConstant.EXCHANGE, ApplicationConstant.STOCK_ROUTING_KEY, message);
    }
}
