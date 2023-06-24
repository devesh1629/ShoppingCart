package com.microservice.paymentservice.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMode {
    private String name;

    public static PaymentMode valueOf(String mode) {
        PaymentMode paymentMode = new PaymentMode();
        paymentMode.name = mode;
        return paymentMode;
    }

    public String name() {
        return this.name;
    }
}
