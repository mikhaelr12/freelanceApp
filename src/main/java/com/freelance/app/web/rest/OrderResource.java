package com.freelance.app.web.rest;

import com.freelance.app.domain.Order;
import com.freelance.app.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
