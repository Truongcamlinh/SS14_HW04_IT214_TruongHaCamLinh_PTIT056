package com.storex.orchestrator.client;

import com.storex.orchestrator.model.CheckoutRequest;
import com.storex.orchestrator.model.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class OrderClient {
    private final WebClient webClient;

    public OrderClient(WebClient.Builder builder,
                       @Value("${services.order-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<OrderDto> create(CheckoutRequest request) {
        return webClient.post().uri("/api/orders")
                .bodyValue(Map.of(
                        "customerId", request.customerId(),
                        "amount", request.originalAmount()))
                .retrieve().bodyToMono(OrderDto.class);
    }

    public Mono<Void> updateAmount(String orderId, BigDecimal amount) {
        return webClient.put().uri("/api/orders/{id}/amount", orderId)
                .bodyValue(Map.of("amount", amount))
                .retrieve().toBodilessEntity().then();
    }

    public Mono<Void> confirm(String orderId) {
        return changeStatus(orderId, "confirm");
    }

    public Mono<Void> cancel(String orderId) {
        return changeStatus(orderId, "cancel");
    }

    private Mono<Void> changeStatus(String orderId, String action) {
        return webClient.put().uri("/api/orders/{id}/{action}", orderId, action)
                .retrieve().toBodilessEntity().then();
    }
}

