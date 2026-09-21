package com.storex.orchestrator.client;

import com.storex.orchestrator.model.PaymentResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PaymentClient {
    private final WebClient webClient;

    public PaymentClient(WebClient.Builder builder,
                         @Value("${services.payment-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<PaymentResult> pay(String orderId, BigDecimal amount) {
        return webClient.post().uri("/api/payments")
                .bodyValue(Map.of("orderId", orderId, "amount", amount))
                .retrieve().bodyToMono(PaymentResult.class);
    }

    public Mono<Void> refund(String orderId) {
        return webClient.post().uri("/api/payments/{orderId}/refund", orderId)
                .retrieve().toBodilessEntity().then();
    }
}

