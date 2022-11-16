package com.danhvv.saga.listener;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.PaymentDto;
import com.danhvv.saga.enums.PaymentStatus;
import com.danhvv.saga.sender.PaymentSender;
import com.danhvv.saga.service.PaymentService;
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
public class PaymentListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentListener.class);

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ApplicationConstant.PAYMENT_QUEUE, durable = "true"),
            exchange = @Exchange(value = ApplicationConstant.EXCHANGE, ignoreDeclarationExceptions = "true"),
            key = ApplicationConstant.PAYMENT_ROUTING_KEY)
    )
    public void listen(Message message) throws IOException {
        PaymentDto paymentDto = objectMapper.readValue(message.getBody(), PaymentDto.class);
        assert paymentDto.getStatus() != null;
        PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentDto.getStatus());
        LOGGER.info("Payment {} message is received. TransactionId :  {}", paymentStatus.name(), paymentDto.getTransactionId());
        switch (paymentStatus) {
            case PAYMENT_REQUESTED:
                paymentService.createPayment(paymentDto);
                break;
            case PAYMENT_PENDING:
            case PAYMENT_FAILED:
                paymentService.updateStatus(paymentDto.getTransactionId(), paymentStatus);
                break;
            case PAYMENT_AVAILABLE:
                paymentService.buyFrom3rdPartyApp(paymentDto);
                break;
            default:
                break;
        }
    }
}
