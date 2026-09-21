package com.storex.orchestrator.controller;

import com.storex.orchestrator.model.CheckoutRequest;
import com.storex.orchestrator.model.CheckoutResult;
import com.storex.orchestrator.service.CheckoutOrchestrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutOrchestrationService service;

    public CheckoutController(CheckoutOrchestrationService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<CheckoutResult> checkout(@RequestBody CheckoutRequest request) {
        return service.checkout(request);
    }
}

