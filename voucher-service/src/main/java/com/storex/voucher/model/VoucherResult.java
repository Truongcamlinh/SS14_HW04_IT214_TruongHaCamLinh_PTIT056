package com.storex.voucher.model;

import java.math.BigDecimal;

public record VoucherResult(
        String orderId,
        String voucherCode,
        BigDecimal discount,
        BigDecimal finalAmount) {
}

