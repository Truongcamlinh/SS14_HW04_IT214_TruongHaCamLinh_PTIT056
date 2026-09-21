package com.storex.orchestrator.client;

import com.storex.orchestrator.model.VoucherResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class VoucherClient {
    private final WebClient webClient;

    public VoucherClient(WebClient.Builder builder,
                         @Value("${services.voucher-url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public Mono<VoucherResult> apply(String orderId, String code, BigDecimal amount) {
        return webClient.post().uri("/api/vouchers/apply")
                .bodyValue(Map.of("orderId", orderId, "voucherCode", code, "amount", amount))
                .retrieve().bodyToMono(VoucherResult.class);
    }

    public Mono<Void> release(String orderId) {
        return webClient.delete().uri("/api/vouchers/reservations/{orderId}", orderId)
                .retrieve().toBodilessEntity().then();
    }
}

