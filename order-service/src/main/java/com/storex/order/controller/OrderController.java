package com.storex.order.controller;

import com.storex.order.model.Order;
import com.storex.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order create(@RequestBody Map<String, Object> body) {
        return service.create((String) body.get("customerId"),
                new BigDecimal(body.get("amount").toString()));
    }

    @PutMapping("/{id}/amount")
    public Order updateAmount(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return service.updateAmount(id, new BigDecimal(body.get("amount").toString()));
    }

    @PutMapping("/{id}/confirm")
    public Order confirm(@PathVariable String id) {
        return service.changeStatus(id, "COMPLETED");
    }

    @PutMapping("/{id}/cancel")
    public Order cancel(@PathVariable String id) {
        return service.changeStatus(id, "CANCELLED");
    }
}

