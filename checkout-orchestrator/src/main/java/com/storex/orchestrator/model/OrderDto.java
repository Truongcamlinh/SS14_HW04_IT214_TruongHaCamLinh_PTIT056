package com.storex.orchestrator.model;

import java.math.BigDecimal;

public record OrderDto(String orderId, String customerId, BigDecimal amount, String status) {
}

