package com.storex.orchestrator.service;

import com.storex.orchestrator.client.OrderClient;
import com.storex.orchestrator.client.PaymentClient;
import com.storex.orchestrator.client.VoucherClient;
import com.storex.orchestrator.model.CheckoutRequest;
import com.storex.orchestrator.model.CheckoutResult;
import com.storex.orchestrator.model.OrderDto;
import com.storex.orchestrator.model.PaymentResult;
import com.storex.orchestrator.model.VoucherResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CheckoutOrchestrationService {
    private final OrderClient orderClient;
    private final VoucherClient voucherClient;
    private final PaymentClient paymentClient;

    public CheckoutOrchestrationService(
            OrderClient orderClient,
            VoucherClient voucherClient,
            PaymentClient paymentClient) {
        this.orderClient = orderClient;
        this.voucherClient = voucherClient;
        this.paymentClient = paymentClient;
    }

    public Mono<CheckoutResult> checkout(CheckoutRequest request) {
        return Mono.defer(() -> {
            SagaContext context = new SagaContext();
            return orderClient.create(request)
                    .doOnNext(context::setOrder)
                    .flatMap(order -> voucherClient.apply(
                            order.orderId(), request.voucherCode(), order.amount()))
                    .doOnNext(context::setVoucher)
                    .flatMap(voucher -> orderClient.updateAmount(
                            context.order.orderId(), voucher.finalAmount()).thenReturn(voucher))
                    .flatMap(voucher -> paymentClient.pay(
                            context.order.orderId(), voucher.finalAmount()))
                    .doOnNext(context::setPayment)
                    .flatMap(payment -> orderClient.confirm(context.order.orderId()).thenReturn(
                            new CheckoutResult(
                                    context.order.orderId(),
                                    "COMPLETED",
                                    context.voucher.finalAmount(),
                                    "Đã áp dụng voucher và thanh toán thành công")))
                    .onErrorResume(error -> compensate(context)
                            .thenReturn(new CheckoutResult(
                                    context.order == null ? null : context.order.orderId(),
                                    "CANCELLED",
                                    context.voucher == null ? request.originalAmount()
                                            : context.voucher.finalAmount(),
                                    "Đã bù trừ do lỗi: " + error.getMessage())));
        });
    }

    private Mono<Void> compensate(SagaContext context) {
        Mono<Void> refund = context.payment == null
                ? Mono.empty()
                : paymentClient.refund(context.order.orderId()).onErrorResume(error -> Mono.empty());
        Mono<Void> releaseVoucher = context.voucher == null
                ? Mono.empty()
                : voucherClient.release(context.order.orderId()).onErrorResume(error -> Mono.empty());
        Mono<Void> cancelOrder = context.order == null
                ? Mono.empty()
                : orderClient.cancel(context.order.orderId()).onErrorResume(error -> Mono.empty());
        return Mono.when(refund, releaseVoucher, cancelOrder);
    }

    private static final class SagaContext {
        private OrderDto order;
        private VoucherResult voucher;
        private PaymentResult payment;

        void setOrder(OrderDto order) {
            this.order = order;
        }

        void setVoucher(VoucherResult voucher) {
            this.voucher = voucher;
        }

        void setPayment(PaymentResult payment) {
            this.payment = payment;
        }
    }
}

