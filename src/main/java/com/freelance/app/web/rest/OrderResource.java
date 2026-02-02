package com.freelance.app.web.rest;

import com.freelance.app.domain.Order;
import com.freelance.app.domain.enumeration.OrderStatus;
import com.freelance.app.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing {@link com.freelance.app.domain.Order}.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderResource {

    private static final Logger LOG = LoggerFactory.getLogger(OrderResource.class);

    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{offerPackageId}")
    public Mono<ResponseEntity<Order>> createOrder(@PathVariable Long offerPackageId) {
        LOG.info("Received request to create order with offerPackageId {}", offerPackageId);
        return orderService.createOrder(offerPackageId).map(ResponseEntity::ok);
    }

    @PatchMapping("/{orderId}/status/{status}")
    public Mono<ResponseEntity<Void>> updateOfferStatus(@PathVariable Long orderId, @PathVariable OrderStatus status) {
        LOG.info("Received request to update order with orderId {}", orderId);
        return orderService.updateOrderStatus(orderId, status).map(ResponseEntity::ok);
    }
    //    @GetMapping("/my")
    //    public Mono<ResponseEntity<List<>>>
}
