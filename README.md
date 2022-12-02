# Tech Stack
- Spring Boot
- RabbitMQ
- H2 Database
- Saga Pattern (Choreography)

## Docker-compose.yaml
Run RabbitMQ with Docker
```
docker-compose up
```

### API Document
Order Service: http://localhost:8081/api-docs

Payment Service: http://localhost:8082/api-docs

Stock Service: http://localhost:8083/api-docs

### Additional Information : 


User sends order request to ORDER service.

ORDER service sends 2 amqp message to PAYMENT service and STOCK service

STOCK service sends related amqp message to ORDER service and PAYMENT service

PAYMENT service sends success or fail to ORDER service.

Statuses that are related with Order, Payment, Stock

* Also you can send multiple order. When one them fails it automatically fails all the orders so be aware of this behaviour.
You can modify stock's listener code for desired behaviour

```
ORDER_RECEIVED, ORDER_COMPLETED, ORDER_PENDING, ORDER_FAILED, ORDER_STOCK_COMPLETED

PAYMENT_REQUESTED, PAYMENT_PENDING, PAYMENT_COMPLETED, PAYMENT_FAILED, PAYMENT_AVAILABLE

STOCK_REQUESTED, STOCK_COMPLETED, STOCK_FAILED, STOCK_PENDING
```

Each service listens the related queue.

### Work-flow : 
1.  Successful Order Case

![image](https://drive.google.com/uc?export=view&id=1IY8bLgpDt0cMr1hAaAQJml8dPTrirmqS)

2.  Failed Check Stock Case

![image](https://drive.google.com/uc?export=view&id=1eBjKbD5b5VBDc_HBxCRYotxXLj-7nDw7)

3.  Failed Payment Case

![image](https://drive.google.com/uc?export=view&id=1bPGH2k4xo9zIMzsT_QW4eJsoyhscF6nL)



