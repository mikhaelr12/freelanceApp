package com.freelance.app.service;

import com.freelance.app.domain.OfferPackage;
import com.freelance.app.domain.Order;
import com.freelance.app.domain.Profile;
import com.freelance.app.domain.enumeration.OrderStatus;
import com.freelance.app.repository.OfferPackageRepository;
import com.freelance.app.repository.OrderRepository;
import com.freelance.app.util.ProfileHelper;
import com.freelance.app.web.rest.errors.ForbiddenAlertException;
import com.freelance.app.web.rest.errors.NotFoundAlertException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.freelance.app.domain.Order}.
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OfferPackageRepository offerPackageRepository;
    private final ProfileHelper profileHelper;

    public OrderService(OrderRepository orderRepository, OfferPackageRepository offerPackageRepository, ProfileHelper profileHelper) {
        this.orderRepository = orderRepository;
        this.offerPackageRepository = offerPackageRepository;
        this.profileHelper = profileHelper;
    }

    public Mono<Order> createOrder(Long offerPackageId) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(
                offerPackageRepository
                    .findById(offerPackageId)
                    .switchIfEmpty(
                        Mono.error(new NotFoundAlertException("Offer package not found", "OfferPackage", "offerPackageNotFound"))
                    )
            )
            .flatMap(tuple -> {
                Profile buyer = tuple.getT1();
                OfferPackage offerPackage = tuple.getT2();

                return orderRepository.save(
                    new Order()
                        .status(OrderStatus.PENDING)
                        .totalAmount(offerPackage.getPrice())
                        .buyer(buyer)
                        .sellerId(offerPackage.getOffer().getOwnerId())
                        .offerpackage(offerPackage)
                );
            });
    }

    public Mono<Void> updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        return profileHelper
            .getCurrentProfile()
            .zipWith(
                orderRepository
                    .findById(orderId)
                    .switchIfEmpty(Mono.error(new NotFoundAlertException("Order not found", "Order", "orderNotFound")))
            )
            .flatMap(tuple -> {
                Profile profile = tuple.getT1();
                Order order = tuple.getT2();
                if (!Objects.equals(profile.getId(), order.getSellerId())) {
                    return Mono.error(
                        new ForbiddenAlertException("Order does not belong to current profile", "Order", "orderNotBelongingToProfile")
                    );
                }
                order.setStatus(orderStatus);
                return orderRepository.save(order).then();
            });
    }
}
