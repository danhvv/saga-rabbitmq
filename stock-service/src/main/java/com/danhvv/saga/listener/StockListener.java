package com.danhvv.saga.listener;

import com.danhvv.saga.constant.ApplicationConstant;
import com.danhvv.saga.dto.StockDto;
import com.danhvv.saga.enums.StockStatus;
import com.danhvv.saga.service.StockService;
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
public class StockListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockListener.class);

    private final ObjectMapper objectMapper;
    private final StockService stockService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = ApplicationConstant.STOCK_QUEUE, durable = "true"),
            exchange = @Exchange(value = ApplicationConstant.EXCHANGE, ignoreDeclarationExceptions = "true"),
            key = ApplicationConstant.STOCK_ROUTING_KEY)
    )
    public void listen(Message message) throws IOException {
        StockDto stockDto = objectMapper.readValue(message.getBody(), StockDto.class);
        StockStatus stockStatus = StockStatus.valueOf(stockDto.getStatus());
        LOGGER.info("Stock {} request is received. TransactionId : {}", stockStatus.name() ,stockDto.getTransactionId());
        switch (stockStatus) {
            case STOCK_PENDING:
            case STOCK_COMPLETED:
                break;
            case STOCK_FAILED:
                stockService.recalculateStockValues(stockDto.getOrders());
                break;
            case STOCK_REQUESTED:
                stockService.prepareStock(stockDto);
                break;
            default:
                break;
        }
    }

}
