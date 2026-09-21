package com.storex.payment.model;

public record PaymentResult(String orderId, String transactionId, String status) {
}

