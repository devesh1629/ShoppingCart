package com.microservice.orderservice.service.impl;

import com.microservice.orderservice.exception.OrderServiceCustomException;
import com.microservice.orderservice.model.Order;
import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.request.PaymentRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.repository.OrderRepository;
import com.microservice.orderservice.service.OrderService;
import com.microservice.orderservice.payload.response.
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("OrderServiceImpl | placeOrder is called");
        // Order Entity -> Save the data with Status Order Created
        // Product Service - Block Products (Reduce the Quantity)
        // Payment Service -> Payments -> Success-> COMPLETE, Else
        // CANCELLED
        log.info("OrderServiceImpl | placeOrder | Placing Order Request orderRequest : " + orderRequest.toString());

        log.info("OrderServiceImpl | placeOrder | Creating Order with Status CREATED");

        Order order = Order.builder()
            .amount(orderRequest.getTotalAmount())
            .orderDate(Instant.now())
            .orderStatus("CREATED")
            .productId(orderRequest.getProductId())
            .quantity(orderRequest.getQuantity())
            .build();

        order = orderRepository.save(order);

        log.info("OrderServiceImpl | placeOrder | Calling Payment Service to complete the payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;

        try {
            log.info("OrderServiceImpl | placeOrder | Payment done Successfully. Changing the Oder status to PLACED");
            orderStatus = "PLACED";
        }
        catch (Exception e) {
            log.error("OrderServiceImpl | placeOrder | Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("OrderServiceImpl | placeOrder | Order Places successfully with Order Id: {}", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {

        log.info("OrderServiceImpl | getOrderDetails | Get order details for Order Id : {}", orderId);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderServiceCustomException("Order not found for the order Id:" +
                                                                orderId, "NOT_FOUND", 404));

        log.info("OrderServiceImpl | getOrderDetails | Invoking Product service to fetch the product for id: {}", order.getProductId());

        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );
    }
}
