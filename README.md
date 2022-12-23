# Tech Stack
- Spring Boot
- RabbitMQ
- H2 Database
- Saga Pattern (Choreography)
- - Distributed Tracing (Jaeger)

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

![demo_success_workflow drawio](https://user-images.githubusercontent.com/50053828/205211082-635a1428-04a9-4cfa-bf01-5ae2c832d918.png)

2.  Failed Check Stock Case

![demo_stock_failed_workflow drawio](https://user-images.githubusercontent.com/50053828/205211134-42028ef3-f165-4e32-87eb-eb4fe78bae2e.png)

3.  Failed Payment Case
![demo_payment_failed_workflow drawio](https://user-images.githubusercontent.com/50053828/205211152-b55c8e32-4471-4016-95b1-4777fc5440ca.png)

### Jaeger
http://localhost:16686/

