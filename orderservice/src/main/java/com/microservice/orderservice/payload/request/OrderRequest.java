package com.microservice.orderservice.payload.request;

import com.microservice.orderservice.utils.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequest {

    private long productId;
    private long quantity;
    private long totalAmount;
    private PaymentMode paymentMode;

}
