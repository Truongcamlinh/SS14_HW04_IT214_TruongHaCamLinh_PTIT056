package com.storex.orchestrator.model;

import java.math.BigDecimal;

public record VoucherResult(
        String orderId,
        String voucherCode,
        BigDecimal discount,
        BigDecimal finalAmount) {
}

