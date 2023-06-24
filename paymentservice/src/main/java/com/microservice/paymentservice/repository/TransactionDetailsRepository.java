package com.microservice.paymentservice.repository;

import com.microservice.paymentservice.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
    Optional<TransactionDetails> findOrderById(long orderId);
}
