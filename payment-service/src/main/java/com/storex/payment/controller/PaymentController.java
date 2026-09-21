package com.storex.payment.controller;

import com.storex.payment.model.PaymentResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final Set<String> paidOrders = ConcurrentHashMap.newKeySet();

    @PostMapping
    public PaymentResult pay(@RequestBody Map<String, Object> body) {
        String orderId = body.get("orderId").toString();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        if (amount.compareTo(new BigDecimal("5000000")) > 0) {
            throw new IllegalArgumentException("Số dư không đủ");
        }
        paidOrders.add(orderId);
        return new PaymentResult(orderId, UUID.randomUUID().toString(), "PAID");
    }

    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> refund(@PathVariable String orderId) {
        paidOrders.remove(orderId);
        return ResponseEntity.noContent().build();
    }
}

