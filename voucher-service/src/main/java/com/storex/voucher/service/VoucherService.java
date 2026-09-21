package com.storex.voucher.service;

import com.storex.voucher.model.VoucherResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VoucherService {
    private final Set<String> reservations = ConcurrentHashMap.newKeySet();

    public VoucherResult apply(String orderId, String code, BigDecimal amount) {
        if (!"SAVE10".equalsIgnoreCase(code)) {
            throw new IllegalArgumentException("Voucher không hợp lệ");
        }
        reservations.add(orderId);
        BigDecimal discount = amount.multiply(new BigDecimal("0.10"));
        return new VoucherResult(orderId, code, discount, amount.subtract(discount));
    }

    public void release(String orderId) {
        reservations.remove(orderId);
    }
}

