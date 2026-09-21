package com.storex.orchestrator.model;

public record PaymentResult(String orderId, String transactionId, String status) {
}

