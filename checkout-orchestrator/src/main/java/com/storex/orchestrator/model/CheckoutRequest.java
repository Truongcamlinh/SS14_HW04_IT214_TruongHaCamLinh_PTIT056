package com.storex.orchestrator.model;

import java.math.BigDecimal;

public record CheckoutRequest(String customerId, String voucherCode, BigDecimal originalAmount) {
}

