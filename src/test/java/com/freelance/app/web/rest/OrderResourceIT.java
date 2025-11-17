//package com.freelance.app.web.rest;
//
//import static com.freelance.app.domain.OrderAsserts.*;
//import static com.freelance.app.web.rest.TestUtil.createUpdateProxyForBean;
//import static com.freelance.app.web.rest.TestUtil.sameNumber;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.Mockito.*;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.freelance.app.IntegrationTest;
//import com.freelance.app.domain.OfferPackage;
//import com.freelance.app.domain.Order;
//import com.freelance.app.domain.User;
//import com.freelance.app.domain.enumeration.OrderStatus;
//import com.freelance.app.repository.EntityManager;
//import com.freelance.app.repository.OfferPackageRepository;
//import com.freelance.app.repository.OrderRepository;
//import com.freelance.app.repository.UserRepository;
//import com.freelance.app.repository.UserRepository;
//import com.freelance.app.repository.UserRepository;
//import com.freelance.app.service.OrderService;
//import com.freelance.app.service.dto.OrderDTO;
//import com.freelance.app.service.mapper.OrderMapper;
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicLong;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Flux;
//
///**
// * Integration tests for the {@link OrderResource} REST controller.
// */
//@IntegrationTest
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
//@WithMockUser
//class OrderResourceIT {
//
//    private static final OrderStatus DEFAULT_STATUS = OrderStatus.PENDING;
//    private static final OrderStatus UPDATED_STATUS = OrderStatus.ACTIVE;
//
//    private static final BigDecimal DEFAULT_TOTAL_AMOUNT = new BigDecimal(1);
//    private static final BigDecimal UPDATED_TOTAL_AMOUNT = new BigDecimal(2);
//    private static final BigDecimal SMALLER_TOTAL_AMOUNT = new BigDecimal(1 - 1);
//
//    private static final String DEFAULT_CURRENCY = "AAA";
//    private static final String UPDATED_CURRENCY = "BBB";
//
//    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
//    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
//
//    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
//    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);
//
//    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";
//
//    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
//    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";
//
//    private static final String ENTITY_API_URL = "/api/orders";
//    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
//
//    private static Random random = new Random();
//    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
//
//    @Autowired
//    private ObjectMapper om;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Mock
//    private OrderRepository orderRepositoryMock;
//
//    @Autowired
//    private OrderMapper orderMapper;
//
//    @Mock
//    private OrderService orderServiceMock;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    private Order order;
//
//    private Order insertedOrder;
//
//    @Autowired
//    private OfferPackageRepository offerPackageRepository;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Order createEntity() {
//        return new Order()
//            .status(DEFAULT_STATUS)
//            .totalAmount(DEFAULT_TOTAL_AMOUNT)
//            .currency(DEFAULT_CURRENCY)
//            .createdDate(DEFAULT_CREATED_DATE)
//            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
//            .createdBy(DEFAULT_CREATED_BY)
//            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
//    }
//
//    /**
//     * Create an updated entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Order createUpdatedEntity() {
//        return new Order()
//            .status(UPDATED_STATUS)
//            .totalAmount(UPDATED_TOTAL_AMOUNT)
//            .currency(UPDATED_CURRENCY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//    }
//
//    public static void deleteEntities(EntityManager em) {
//        try {
//            em.deleteAll(Order.class).block();
//        } catch (Exception e) {
//            // It can fail, if other entities are still referring this - it will be removed later.
//        }
//    }
//
//    @BeforeEach
//    void initTest() {
//        order = createEntity();
//    }
//
//    @AfterEach
//    void cleanup() {
//        if (insertedOrder != null) {
//            orderRepository.delete(insertedOrder).block();
//            insertedOrder = null;
//        }
//        deleteEntities(em);
//        userRepository.deleteAllUserAuthorities().block();
//        userRepository.deleteAll().block();
//    }
//
//    @Test
//    void createOrder() throws Exception {
//        long databaseSizeBeforeCreate = getRepositoryCount();
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//        var returnedOrderDTO = webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isCreated()
//            .expectBody(OrderDTO.class)
//            .returnResult()
//            .getResponseBody();
//
//        // Validate the Order in the database
//        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
//        var returnedOrder = orderMapper.toEntity(returnedOrderDTO);
//        assertOrderUpdatableFieldsEquals(returnedOrder, getPersistedOrder(returnedOrder));
//
//        insertedOrder = returnedOrder;
//    }
//
//    @Test
//    void createOrderWithExistingId() throws Exception {
//        // Create the Order with an existing ID
//        order.setId(1L);
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        long databaseSizeBeforeCreate = getRepositoryCount();
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeCreate);
//    }
//
//    @Test
//    void checkStatusIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        order.setStatus(null);
//
//        // Create the Order, which fails.
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkTotalAmountIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        order.setTotalAmount(null);
//
//        // Create the Order, which fails.
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkCurrencyIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        order.setCurrency(null);
//
//        // Create the Order, which fails.
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void checkCreatedDateIsRequired() throws Exception {
//        long databaseSizeBeforeTest = getRepositoryCount();
//        // set the field null
//        order.setCreatedDate(null);
//
//        // Create the Order, which fails.
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        webTestClient
//            .post()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        assertSameRepositoryCount(databaseSizeBeforeTest);
//    }
//
//    @Test
//    void getAllOrders() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.[*].id")
//            .value(hasItem(order.getId().intValue()))
//            .jsonPath("$.[*].status")
//            .value(hasItem(DEFAULT_STATUS.toString()))
//            .jsonPath("$.[*].totalAmount")
//            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
//            .jsonPath("$.[*].currency")
//            .value(hasItem(DEFAULT_CURRENCY))
//            .jsonPath("$.[*].createdDate")
//            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.[*].lastModifiedDate")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.[*].createdBy")
//            .value(hasItem(DEFAULT_CREATED_BY))
//            .jsonPath("$.[*].lastModifiedBy")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    void getAllOrdersWithEagerRelationshipsIsEnabled() {
//        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());
//
//        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();
//
//        verify(orderServiceMock, times(1)).findAllWithEagerRelationships(any());
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    void getAllOrdersWithEagerRelationshipsIsNotEnabled() {
//        when(orderServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());
//
//        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
//        verify(orderRepositoryMock, times(1)).findAllWithEagerRelationships(any());
//    }
//
//    @Test
//    void getOrder() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get the order
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, order.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.id")
//            .value(is(order.getId().intValue()))
//            .jsonPath("$.status")
//            .value(is(DEFAULT_STATUS.toString()))
//            .jsonPath("$.totalAmount")
//            .value(is(sameNumber(DEFAULT_TOTAL_AMOUNT)))
//            .jsonPath("$.currency")
//            .value(is(DEFAULT_CURRENCY))
//            .jsonPath("$.createdDate")
//            .value(is(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.lastModifiedDate")
//            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.createdBy")
//            .value(is(DEFAULT_CREATED_BY))
//            .jsonPath("$.lastModifiedBy")
//            .value(is(DEFAULT_LAST_MODIFIED_BY));
//    }
//
//    @Test
//    void getOrdersByIdFiltering() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        Long id = order.getId();
//
//        defaultOrderFiltering("id.equals=" + id, "id.notEquals=" + id);
//
//        defaultOrderFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);
//
//        defaultOrderFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
//    }
//
//    @Test
//    void getAllOrdersByStatusIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where status equals to
//        defaultOrderFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
//    }
//
//    @Test
//    void getAllOrdersByStatusIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where status in
//        defaultOrderFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
//    }
//
//    @Test
//    void getAllOrdersByStatusIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where status is not null
//        defaultOrderFiltering("status.specified=true", "status.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount equals to
//        defaultOrderFiltering("totalAmount.equals=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.equals=" + UPDATED_TOTAL_AMOUNT);
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount in
//        defaultOrderFiltering(
//            "totalAmount.in=" + DEFAULT_TOTAL_AMOUNT + "," + UPDATED_TOTAL_AMOUNT,
//            "totalAmount.in=" + UPDATED_TOTAL_AMOUNT
//        );
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount is not null
//        defaultOrderFiltering("totalAmount.specified=true", "totalAmount.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsGreaterThanOrEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount is greater than or equal to
//        defaultOrderFiltering(
//            "totalAmount.greaterThanOrEqual=" + DEFAULT_TOTAL_AMOUNT,
//            "totalAmount.greaterThanOrEqual=" + UPDATED_TOTAL_AMOUNT
//        );
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsLessThanOrEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount is less than or equal to
//        defaultOrderFiltering("totalAmount.lessThanOrEqual=" + DEFAULT_TOTAL_AMOUNT, "totalAmount.lessThanOrEqual=" + SMALLER_TOTAL_AMOUNT);
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsLessThanSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount is less than
//        defaultOrderFiltering("totalAmount.lessThan=" + UPDATED_TOTAL_AMOUNT, "totalAmount.lessThan=" + DEFAULT_TOTAL_AMOUNT);
//    }
//
//    @Test
//    void getAllOrdersByTotalAmountIsGreaterThanSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where totalAmount is greater than
//        defaultOrderFiltering("totalAmount.greaterThan=" + SMALLER_TOTAL_AMOUNT, "totalAmount.greaterThan=" + DEFAULT_TOTAL_AMOUNT);
//    }
//
//    @Test
//    void getAllOrdersByCurrencyIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where currency equals to
//        defaultOrderFiltering("currency.equals=" + DEFAULT_CURRENCY, "currency.equals=" + UPDATED_CURRENCY);
//    }
//
//    @Test
//    void getAllOrdersByCurrencyIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where currency in
//        defaultOrderFiltering("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY, "currency.in=" + UPDATED_CURRENCY);
//    }
//
//    @Test
//    void getAllOrdersByCurrencyIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where currency is not null
//        defaultOrderFiltering("currency.specified=true", "currency.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByCurrencyContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where currency contains
//        defaultOrderFiltering("currency.contains=" + DEFAULT_CURRENCY, "currency.contains=" + UPDATED_CURRENCY);
//    }
//
//    @Test
//    void getAllOrdersByCurrencyNotContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where currency does not contain
//        defaultOrderFiltering("currency.doesNotContain=" + UPDATED_CURRENCY, "currency.doesNotContain=" + DEFAULT_CURRENCY);
//    }
//
//    @Test
//    void getAllOrdersByCreatedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdDate equals to
//        defaultOrderFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
//    }
//
//    @Test
//    void getAllOrdersByCreatedDateIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdDate in
//        defaultOrderFiltering(
//            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
//            "createdDate.in=" + UPDATED_CREATED_DATE
//        );
//    }
//
//    @Test
//    void getAllOrdersByCreatedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdDate is not null
//        defaultOrderFiltering("createdDate.specified=true", "createdDate.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedDateIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedDate equals to
//        defaultOrderFiltering(
//            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
//            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedDateIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedDate in
//        defaultOrderFiltering(
//            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
//            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
//        );
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedDateIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedDate is not null
//        defaultOrderFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByCreatedByIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdBy equals to
//        defaultOrderFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOrdersByCreatedByIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdBy in
//        defaultOrderFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOrdersByCreatedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdBy is not null
//        defaultOrderFiltering("createdBy.specified=true", "createdBy.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByCreatedByContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdBy contains
//        defaultOrderFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
//    }
//
//    @Test
//    void getAllOrdersByCreatedByNotContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where createdBy does not contain
//        defaultOrderFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedByIsEqualToSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedBy equals to
//        defaultOrderFiltering("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedByIsInShouldWork() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedBy in
//        defaultOrderFiltering(
//            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedByIsNullOrNotNull() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedBy is not null
//        defaultOrderFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedByContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedBy contains
//        defaultOrderFiltering("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY, "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
//    }
//
//    @Test
//    void getAllOrdersByLastModifiedByNotContainsSomething() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        // Get all the orderList where lastModifiedBy does not contain
//        defaultOrderFiltering(
//            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
//            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
//        );
//    }
//
//    @Test
//    void getAllOrdersByBuyerIsEqualToSomething() {
//        User buyer = UserResourceIT.createEntity();
//        userRepository.save(buyer).block();
//        Long buyerId = buyer.getId();
//        order.setBuyerId(buyerId);
//        insertedOrder = orderRepository.save(order).block();
//        // Get all the orderList where buyer equals to buyerId
//        defaultOrderShouldBeFound("buyerId.equals=" + buyerId);
//
//        // Get all the orderList where buyer equals to (buyerId + 1)
//        defaultOrderShouldNotBeFound("buyerId.equals=" + (buyerId + 1));
//    }
//
//    @Test
//    void getAllOrdersBySellerIsEqualToSomething() {
//        User seller = UserResourceIT.createEntity();
//        userRepository.save(seller).block();
//        Long sellerId = seller.getId();
//        order.setSellerId(sellerId);
//        insertedOrder = orderRepository.save(order).block();
//        // Get all the orderList where seller equals to sellerId
//        defaultOrderShouldBeFound("sellerId.equals=" + sellerId);
//
//        // Get all the orderList where seller equals to (sellerId + 1)
//        defaultOrderShouldNotBeFound("sellerId.equals=" + (sellerId + 1));
//    }
//
//    @Test
//    void getAllOrdersByOfferpackageIsEqualToSomething() {
//        OfferPackage offerpackage = OfferPackageResourceIT.createEntity();
//        offerPackageRepository.save(offerpackage).block();
//        Long offerpackageId = offerpackage.getId();
//        order.setOfferpackageId(offerpackageId);
//        insertedOrder = orderRepository.save(order).block();
//        // Get all the orderList where offerpackage equals to offerpackageId
//        defaultOrderShouldBeFound("offerpackageId.equals=" + offerpackageId);
//
//        // Get all the orderList where offerpackage equals to (offerpackageId + 1)
//        defaultOrderShouldNotBeFound("offerpackageId.equals=" + (offerpackageId + 1));
//    }
//
//    private void defaultOrderFiltering(String shouldBeFound, String shouldNotBeFound) {
//        defaultOrderShouldBeFound(shouldBeFound);
//        defaultOrderShouldNotBeFound(shouldNotBeFound);
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is returned.
//     */
//    private void defaultOrderShouldBeFound(String filter) {
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$.[*].id")
//            .value(hasItem(order.getId().intValue()))
//            .jsonPath("$.[*].status")
//            .value(hasItem(DEFAULT_STATUS.toString()))
//            .jsonPath("$.[*].totalAmount")
//            .value(hasItem(sameNumber(DEFAULT_TOTAL_AMOUNT)))
//            .jsonPath("$.[*].currency")
//            .value(hasItem(DEFAULT_CURRENCY))
//            .jsonPath("$.[*].createdDate")
//            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
//            .jsonPath("$.[*].lastModifiedDate")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
//            .jsonPath("$.[*].createdBy")
//            .value(hasItem(DEFAULT_CREATED_BY))
//            .jsonPath("$.[*].lastModifiedBy")
//            .value(hasItem(DEFAULT_LAST_MODIFIED_BY));
//
//        // Check, that the count call also returns 1
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .value(is(1));
//    }
//
//    /**
//     * Executes the search, and checks that the default entity is not returned.
//     */
//    private void defaultOrderShouldNotBeFound(String filter) {
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .isArray()
//            .jsonPath("$")
//            .isEmpty();
//
//        // Check, that the count call also returns 0
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk()
//            .expectHeader()
//            .contentType(MediaType.APPLICATION_JSON)
//            .expectBody()
//            .jsonPath("$")
//            .value(is(0));
//    }
//
//    @Test
//    void getNonExistingOrder() {
//        // Get the order
//        webTestClient
//            .get()
//            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
//            .accept(MediaType.APPLICATION_PROBLEM_JSON)
//            .exchange()
//            .expectStatus()
//            .isNotFound();
//    }
//
//    @Test
//    void putExistingOrder() throws Exception {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the order
//        Order updatedOrder = orderRepository.findById(order.getId()).block();
//        updatedOrder
//            .status(UPDATED_STATUS)
//            .totalAmount(UPDATED_TOTAL_AMOUNT)
//            .currency(UPDATED_CURRENCY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);
//
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, orderDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertPersistedOrderToMatchAllProperties(updatedOrder);
//    }
//
//    @Test
//    void putNonExistingOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, orderDTO.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithIdMismatchOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void putWithMissingIdPathParamOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .put()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void partialUpdateOrderWithPatch() throws Exception {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the order using partial update
//        Order partialUpdatedOrder = new Order();
//        partialUpdatedOrder.setId(order.getId());
//
//        partialUpdatedOrder
//            .totalAmount(UPDATED_TOTAL_AMOUNT)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedOrder))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Order in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertOrderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOrder, order), getPersistedOrder(order));
//    }
//
//    @Test
//    void fullUpdateOrderWithPatch() throws Exception {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//
//        // Update the order using partial update
//        Order partialUpdatedOrder = new Order();
//        partialUpdatedOrder.setId(order.getId());
//
//        partialUpdatedOrder
//            .status(UPDATED_STATUS)
//            .totalAmount(UPDATED_TOTAL_AMOUNT)
//            .currency(UPDATED_CURRENCY)
//            .createdDate(UPDATED_CREATED_DATE)
//            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
//            .createdBy(UPDATED_CREATED_BY)
//            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
//
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, partialUpdatedOrder.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(partialUpdatedOrder))
//            .exchange()
//            .expectStatus()
//            .isOk();
//
//        // Validate the Order in the database
//
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//        assertOrderUpdatableFieldsEquals(partialUpdatedOrder, getPersistedOrder(partialUpdatedOrder));
//    }
//
//    @Test
//    void patchNonExistingOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, orderDTO.getId())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithIdMismatchOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isBadRequest();
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void patchWithMissingIdPathParamOrder() throws Exception {
//        long databaseSizeBeforeUpdate = getRepositoryCount();
//        order.setId(longCount.incrementAndGet());
//
//        // Create the Order
//        OrderDTO orderDTO = orderMapper.toDto(order);
//
//        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
//        webTestClient
//            .patch()
//            .uri(ENTITY_API_URL)
//            .contentType(MediaType.valueOf("application/merge-patch+json"))
//            .bodyValue(om.writeValueAsBytes(orderDTO))
//            .exchange()
//            .expectStatus()
//            .isEqualTo(405);
//
//        // Validate the Order in the database
//        assertSameRepositoryCount(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    void deleteOrder() {
//        // Initialize the database
//        insertedOrder = orderRepository.save(order).block();
//
//        long databaseSizeBeforeDelete = getRepositoryCount();
//
//        // Delete the order
//        webTestClient
//            .delete()
//            .uri(ENTITY_API_URL_ID, order.getId())
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isNoContent();
//
//        // Validate the database contains one less item
//        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
//    }
//
//    protected long getRepositoryCount() {
//        return orderRepository.count().block();
//    }
//
//    protected void assertIncrementedRepositoryCount(long countBefore) {
//        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
//    }
//
//    protected void assertDecrementedRepositoryCount(long countBefore) {
//        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
//    }
//
//    protected void assertSameRepositoryCount(long countBefore) {
//        assertThat(countBefore).isEqualTo(getRepositoryCount());
//    }
//
//    protected Order getPersistedOrder(Order order) {
//        return orderRepository.findById(order.getId()).block();
//    }
//
//    protected void assertPersistedOrderToMatchAllProperties(Order expectedOrder) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertOrderAllPropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
//        assertOrderUpdatableFieldsEquals(expectedOrder, getPersistedOrder(expectedOrder));
//    }
//
//    protected void assertPersistedOrderToMatchUpdatableProperties(Order expectedOrder) {
//        // Test fails because reactive api returns an empty object instead of null
//        // assertOrderAllUpdatablePropertiesEquals(expectedOrder, getPersistedOrder(expectedOrder));
//        assertOrderUpdatableFieldsEquals(expectedOrder, getPersistedOrder(expectedOrder));
//    }
//}
