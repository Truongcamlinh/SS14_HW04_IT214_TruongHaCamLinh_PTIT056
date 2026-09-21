package com.storex.order.model;

import java.math.BigDecimal;

public class Order {
    private final String orderId;
    private final String customerId;
    private BigDecimal amount;
    private String status;

    public Order(String orderId, String customerId, BigDecimal amount, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}

