package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.request.OrderMatchRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderResponseStatus;
import com.erdemiryigit.brokagefirm.entity.Asset;
import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.exception.CustomerAssetException;
import com.erdemiryigit.brokagefirm.exception.OrderStatusException;
import com.erdemiryigit.brokagefirm.repository.AssetRepository;
import com.erdemiryigit.brokagefirm.repository.CustomerAssetRepository;
import com.erdemiryigit.brokagefirm.repository.CustomerRepository;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.service.impl.OrderServiceImpl;
import com.erdemiryigit.brokagefirm.util.OrderMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import org.junit.jupiter.api.Assertions;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerAssetRepository customerAssetRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Rollback(value = false)
    @Test
    void whenEnoughFundsThenCreateOrder() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("TSLA", "Tesla");

        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(100))
                .build();
        customerAssetRepository.save(customerFunds);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), asset.getTicker(), Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(10));
        orderService.createOrder(request);

        CustomerAsset customerFundsAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(customer.getId(), tryAsset.getTicker()).get();

        BigDecimal orderAmount = request.price().multiply(request.size());

        BigDecimal remainingFunds = customerFunds.getUsableSize().subtract(orderAmount);

        Assertions.assertEquals(customerFundsAfterOrder.getUsableSize().compareTo(remainingFunds), 0);
    }

    @Rollback(value = false)
    @Test
    void whenCreateOrderWithNotEnoughFundsThenThrow() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("TSLA", "Tesla");

        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(100)).build();
        customerFunds = customerAssetRepository.save(customerFunds);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), asset.getTicker(), Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(999));

        Assertions.assertThrows(RuntimeException.class, () -> orderService.createOrder(request));

        CustomerAsset customerFundsAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(customer.getId(), tryAsset.getTicker()).get();

        Assertions.assertEquals(customerFunds.getUsableSize().compareTo(customerFundsAfterOrder.getUsableSize()), 0);

        Assertions.assertEquals(customerFunds.getSize().compareTo(customerFundsAfterOrder.getSize()), 0);
    }


    @Rollback(value = false)
    @Test
    void whenDeleteBuyOrderThenFundsAreReleased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Create an order
        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), asset.getTicker(),
                Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        // Verify funds were locked
        CustomerAsset customerFundsAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        BigDecimal orderAmount = request.price().multiply(request.size());
        BigDecimal expectedLockedBalance = initialBalance.subtract(orderAmount);
        Assertions.assertEquals(0, customerFundsAfterOrder.getUsableSize().compareTo(expectedLockedBalance));

        // Delete the order
        orderService.deleteOrder(createdOrder.id());

        // Verify order was canceled
        Assertions.assertEquals(orderRepository.findById(createdOrder.id()).get().getStatus(), Order.OrderStatus.CANCELLED);

        // Verify funds were released
        CustomerAsset customerFundsAfterDelete = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        Assertions.assertEquals(0, customerFundsAfterDelete.getUsableSize().compareTo(initialBalance));
    }

    @Rollback(value = false)
    @Test
    void whenDeleteSellOrderThenAssetIsReturned() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Setup customer asset
        BigDecimal initialAssetAmount = BigDecimal.valueOf(10);
        BigDecimal initialUsableAsset = BigDecimal.valueOf(10);
        CustomerAsset customerAsset = CustomerAsset.builder()
                .asset(teslaAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        // Create an order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                teslaAsset.getTicker(),
                Order.OrderSide.SELL,
                BigDecimal.ONE,
                BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        // Verify funds were locked
        CustomerAsset customerAssetAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), teslaAsset.getTicker()).get();

        BigDecimal expectedUsableSizeAfterOrder = customerAsset.getUsableSize().subtract(createdOrder.size());

        Assertions.assertEquals(0, customerAssetAfterOrder.getUsableSize().compareTo(expectedUsableSizeAfterOrder));

        // Delete the order
        orderService.deleteOrder(createdOrder.id());

        // Verify order was canceled
        Assertions.assertEquals(orderRepository.findById(createdOrder.id()).get().getStatus(), Order.OrderStatus.CANCELLED);

        // Verify funds were released
        CustomerAsset customerAssetAfterDelete = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), teslaAsset.getTicker()).get();
        Assertions.assertEquals(0, customerAssetAfterDelete.getUsableSize().compareTo(initialAssetAmount));
    }


    @Rollback(value = false)
    @Test
    void whenDeleteNonPendingOrderThenThrowOrderStatusException() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Setup customer asset
        BigDecimal initialAssetAmount = BigDecimal.valueOf(10);
        BigDecimal initialUsableAsset = BigDecimal.valueOf(10);
        CustomerAsset customerAsset = CustomerAsset.builder()
                .asset(teslaAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), teslaAsset.getTicker(),
                Order.OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);
        orderService.deleteOrder(createdOrder.id());

        Assertions.assertThrows(OrderStatusException.class, () -> orderService.deleteOrder(createdOrder.id()));
    }

    @Rollback(value = false)
    @Test
    void whenDeleteOrderWithNonExistentCustomerAssetThenThrowRuntimeException() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Setup customer asset
        BigDecimal initialAssetAmount = BigDecimal.valueOf(10);
        BigDecimal initialUsableAsset = BigDecimal.valueOf(10);
        CustomerAsset customerAsset = CustomerAsset.builder()
                .asset(teslaAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                teslaAsset.getTicker(),
                Order.OrderSide.SELL,
                BigDecimal.ONE,
                BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        customerAssetRepository.deleteAll();

        Assertions.assertThrows(RuntimeException.class, () -> orderService.deleteOrder(createdOrder.id()));
    }


    @Rollback(value = false)
    @Test
    void whenMatchBuyOrderThenFundsReducedAndAssetIncreased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Create order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                teslaAsset.getTicker(),
                Order.OrderSide.BUY,
                BigDecimal.valueOf(10), // price
                BigDecimal.valueOf(5)   // size
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        OrderMatchRequest orderMatchRequest = OrderMatchRequest.builder()
                .id(createdOrder.id())
                .build();

        // Match the order
        OrderMatchResponse matchedOrder = orderService.matchOrder(orderMatchRequest);

        // todo matchresponse statusu duzelt
        // Verify order status is MATCHED
        Assertions.assertEquals(OrderResponseStatus.SUCCESSFUL, matchedOrder.orderResponseStatus());

        // Verify funds are reduced
        CustomerAsset customerFundsAfterMatch = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        BigDecimal orderAmount = request.price().multiply(request.size());
        BigDecimal expectedBalance = initialBalance.subtract(orderAmount);
        Assertions.assertEquals(0, customerFundsAfterMatch.getSize().compareTo(expectedBalance));

        // Verify asset is increased
        CustomerAsset customerAssetAfterMatch = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), teslaAsset.getTicker()).get();
        Assertions.assertEquals(0, customerAssetAfterMatch.getSize().compareTo(request.size()));
    }

    @Rollback(value = false)
    @Test
    void whenMatchSellOrderThenAssetReducedAndFundsIncreased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Setup customer asset
        BigDecimal initialAssetAmount = BigDecimal.valueOf(10);
        CustomerAsset customerAsset = CustomerAsset.builder()
                .asset(teslaAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialAssetAmount)
                .build();
        customerAssetRepository.save(customerAsset);

        // Create order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                teslaAsset.getTicker(),
                Order.OrderSide.SELL,
                BigDecimal.valueOf(10), // price
                BigDecimal.valueOf(5)   // size
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        OrderMatchRequest orderMatchRequest = OrderMatchRequest.builder()
                .id(createdOrder.id())
                .build();

        // Match the order
        OrderMatchResponse matchedOrder = orderService.matchOrder(orderMatchRequest);

        // todo matchresponse statusu duzelt
        // Verify order status is MATCHED
        Assertions.assertEquals(OrderResponseStatus.SUCCESSFUL, matchedOrder.orderResponseStatus());

        // Verify funds are increased
        CustomerAsset customerFundsAfterMatch = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        BigDecimal orderAmount = request.price().multiply(request.size());
        BigDecimal expectedBalance = initialBalance.add(orderAmount);
        Assertions.assertEquals(0, customerFundsAfterMatch.getSize().compareTo(expectedBalance));

        // Verify asset is reduced
        CustomerAsset customerAssetAfterMatch = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), teslaAsset.getTicker()).get();
        BigDecimal expectedAssetAmount = initialAssetAmount.subtract(request.size());
        Assertions.assertEquals(0, customerAssetAfterMatch.getSize().compareTo(expectedAssetAmount));
    }

    @Rollback(value = false)
    @Test
    void whenMatchNonPendingOrderThenThrowOrderStatusException() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Create and match an order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                teslaAsset.getTicker(),
                Order.OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(5)
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        OrderMatchRequest orderMatchRequest = OrderMatchRequest.builder()
                .id(createdOrder.id())
                .build();
        OrderMatchResponse matchedOrder = orderService.matchOrder(orderMatchRequest);

        // Try to match it again
        Assertions.assertThrows(OrderStatusException.class, () -> orderService.matchOrder(orderMatchRequest));
    }

    @Rollback(value = false)
    @Test
    void whenMatchOrderWithNonExistentFundsThenThrowCustomerAssetException() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Create order directly without setting up customer funds
        Order order = Order.builder()
                .customer(customer)
                .asset(teslaAsset)
                .orderSide(Order.OrderSide.BUY)
                .size(BigDecimal.valueOf(5))
                .price(BigDecimal.valueOf(10))
                .status(Order.OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        // Try to match the order
        Order finalOrder = order;

        OrderMatchRequest orderMatchRequest = OrderMatchRequest.builder()
                .id(finalOrder.getId())
                .build();

        Assertions.assertThrows(CustomerAssetException.class, () -> orderService.matchOrder(orderMatchRequest));
    }

    @Rollback(value = false)
    @Test
    void whenMatchSellOrderWithNonExistentAssetThenThrowCustomerAssetException() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset teslaAsset = new Asset("TSLA", "Tesla");
        assetRepository.save(tryAsset);
        assetRepository.save(teslaAsset);

        // Setup customer
        Customer customer = Customer.builder().name("John Doe").build();
        customerRepository.save(customer);

        // Setup customer funds but not the asset
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Create a SELL order
        Order order = Order.builder()
                .customer(customer)
                .asset(teslaAsset)
                .orderSide(Order.OrderSide.SELL)
                .size(BigDecimal.valueOf(5))
                .price(BigDecimal.valueOf(10))
                .status(Order.OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        // Try to match the order
        Order finalOrder = order;

        OrderMatchRequest orderMatchRequest = OrderMatchRequest.builder()
                .id(finalOrder.getId())
                .build();

        Assertions.assertThrows(CustomerAssetException.class, () -> orderService.matchOrder(orderMatchRequest));
    }

}