package com.storex.orchestrator.model;

import java.math.BigDecimal;

public record CheckoutResult(
        String orderId,
        String status,
        BigDecimal finalAmount,
        String message) {
}

