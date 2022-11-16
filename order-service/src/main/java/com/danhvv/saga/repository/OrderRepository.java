package com.danhvv.saga.repository;

import com.danhvv.saga.entity.Order;
import com.danhvv.saga.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByTransactionId(String transactionId);

    @Transactional(propagation = Propagation.MANDATORY)
    void deleteByTransactionId(String transactionId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<Order> findByStatus(OrderStatus orderStatus);
}
