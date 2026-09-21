package com.storex.order.service;

import com.storex.order.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order create(String customerId, BigDecimal amount) {
        Order order = new Order(UUID.randomUUID().toString(), customerId, amount, "PENDING");
        orders.put(order.getOrderId(), order);
        return order;
    }

    public Order updateAmount(String orderId, BigDecimal amount) {
        Order order = get(orderId);
        order.setAmount(amount);
        return order;
    }

    public Order changeStatus(String orderId, String status) {
        Order order = get(orderId);
        order.setStatus(status);
        return order;
    }

    private Order get(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng");
        }
        return order;
    }
}

