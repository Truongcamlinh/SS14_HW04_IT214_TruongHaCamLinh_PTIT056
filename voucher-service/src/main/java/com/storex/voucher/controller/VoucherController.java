package com.storex.voucher.controller;

import com.storex.voucher.model.VoucherResult;
import com.storex.voucher.service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    private final VoucherService service;

    public VoucherController(VoucherService service) {
        this.service = service;
    }

    @PostMapping("/apply")
    public VoucherResult apply(@RequestBody Map<String, Object> body) {
        return service.apply(
                body.get("orderId").toString(),
                body.get("voucherCode").toString(),
                new BigDecimal(body.get("amount").toString()));
    }

    @DeleteMapping("/reservations/{orderId}")
    public ResponseEntity<Void> release(@PathVariable String orderId) {
        service.release(orderId);
        return ResponseEntity.noContent().build();
    }
}

