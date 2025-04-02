package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderResponseStatus;
import com.erdemiryigit.brokagefirm.entity.Asset;
import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.exception.CustomerAssetInsufficientException;
import com.erdemiryigit.brokagefirm.exception.CustomerAssetNotFoundException;
import com.erdemiryigit.brokagefirm.exception.OrderStatusException;
import com.erdemiryigit.brokagefirm.repository.AssetRepository;
import com.erdemiryigit.brokagefirm.repository.CustomerAssetRepository;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import com.erdemiryigit.brokagefirm.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerAssetRepository customerAssetRepository;

    @Autowired
    private OrderServiceImpl orderService;

    @Test
    void whenEnoughFundsThenCreateOrder() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");

        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(100))
                .build();
        customerAssetRepository.save(customerFunds);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), asset.getTicker(), OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(10));
        orderService.createOrder(request);

        CustomerAsset customerFundsAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(customer.getId(), tryAsset.getTicker()).get();

        BigDecimal remainingFunds = customerFunds.getUsableSize();

        Assertions.assertEquals(customerFundsAfterOrder.getUsableSize().compareTo(remainingFunds), 0);
    }

    @Test
    void whenCreateOrderWithNotEnoughFundsThenThrow() {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");

        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(100)).build();
        customerFunds = customerAssetRepository.save(customerFunds);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), asset.getTicker(), OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(999));

        Assertions.assertThrows(CustomerAssetInsufficientException.class, () -> orderService.createOrder(request));

        CustomerAsset customerFundsAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(customer.getId(), tryAsset.getTicker()).get();

        Assertions.assertEquals(customerFunds.getUsableSize().compareTo(customerFundsAfterOrder.getUsableSize()), 0);

        Assertions.assertEquals(customerFunds.getSize().compareTo(customerFundsAfterOrder.getSize()), 0);
    }


    @Test
    void whenDeleteBuyOrderThenFundsAreReleased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset asset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(asset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                asset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(10));
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
        Assertions.assertEquals(orderRepository.findById(createdOrder.id()).get().getStatus(), OrderStatus.CANCELLED);

        // Verify funds were released
        CustomerAsset customerFundsAfterDelete = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        Assertions.assertEquals(0, customerFundsAfterDelete.getUsableSize().compareTo(initialBalance));
    }

    @Test
    void whenDeleteSellOrderThenAssetIsReturned() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                .asset(stockAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        // Create an order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.ONE,
                BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        // Verify funds were locked
        CustomerAsset customerAssetAfterOrder = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();

        BigDecimal expectedUsableSizeAfterOrder = customerAsset.getUsableSize();

        Assertions.assertEquals(0, customerAssetAfterOrder.getUsableSize().compareTo(expectedUsableSizeAfterOrder));

        // Delete the order
        orderService.deleteOrder(createdOrder.id());

        // Verify order was canceled
        Assertions.assertEquals(orderRepository.findById(createdOrder.id()).get().getStatus(), OrderStatus.CANCELLED);

        // Verify funds were released
        CustomerAsset customerAssetAfterDelete = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();
        Assertions.assertEquals(0, customerAssetAfterDelete.getUsableSize().compareTo(initialAssetAmount));
    }

    @Test
    void whenCancelingMultipleBuyOrdersThenAllFundsAreReleased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Create first buy order
        OrderCreateRequest firstRequest = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(2),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse firstOrder = orderService.createOrder(firstRequest);

        // Create second buy order
        OrderCreateRequest secondRequest = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(3),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse secondOrder = orderService.createOrder(secondRequest);

        // Verify funds were locked for both orders
        CustomerAsset fundsAfterOrders = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();

        BigDecimal totalOrderAmount =
                firstRequest.price().multiply(firstRequest.size())
                        .add(secondRequest.price().multiply(secondRequest.size()));
        BigDecimal expectedUsableBalance = initialBalance.subtract(totalOrderAmount);

        Assertions.assertEquals(0, fundsAfterOrders.getUsableSize().compareTo(expectedUsableBalance));

        // Cancel both orders
        orderService.deleteOrder(firstOrder.id());
        orderService.deleteOrder(secondOrder.id());

        // Verify all funds were released
        CustomerAsset fundsAfterCancellations = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        Assertions.assertEquals(0, fundsAfterCancellations.getUsableSize().compareTo(initialBalance));
    }

    @Test
    void whenCancelingMultipleSellOrdersThenAllAssetsAreReturned() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
        BigDecimal initialAssetAmount = BigDecimal.valueOf(20);
        CustomerAsset customerAsset = CustomerAsset.builder()
                .asset(stockAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialAssetAmount)
                .build();
        customerAssetRepository.save(customerAsset);

        // Create first sell order
        OrderCreateRequest firstRequest = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.valueOf(5),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse firstOrder = orderService.createOrder(firstRequest);

        // Create second sell order
        OrderCreateRequest secondRequest = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.valueOf(7),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse secondOrder = orderService.createOrder(secondRequest);

        // Verify assets were locked for both orders
        CustomerAsset assetsAfterOrders = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();

        BigDecimal totalLockedAssets = firstRequest.size().add(secondRequest.size());
        BigDecimal expectedUsableAssets = initialAssetAmount.subtract(totalLockedAssets);

        Assertions.assertEquals(0, assetsAfterOrders.getUsableSize().compareTo(expectedUsableAssets));

        // Cancel both orders
        orderService.deleteOrder(firstOrder.id());
        orderService.deleteOrder(secondOrder.id());

        // Verify all assets were returned
        CustomerAsset assetsAfterCancellations = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();
        Assertions.assertEquals(0, assetsAfterCancellations.getUsableSize().compareTo(initialAssetAmount));
    }

    @Test
    void whenCancelingBuyAndSellOrdersThenBothFundsAndAssetsAreReturned() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        Asset appleAsset = new Asset("AAPL", "Apple");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);
        assetRepository.save(appleAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

        // Setup customer funds
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        CustomerAsset customerFunds = CustomerAsset.builder()
                .asset(tryAsset)
                .customer(customer)
                .size(initialBalance)
                .usableSize(initialBalance)
                .build();
        customerAssetRepository.save(customerFunds);

        // Setup customer stock asset
        BigDecimal initialStockAmount = BigDecimal.valueOf(15);
        CustomerAsset customerStockAsset = CustomerAsset.builder()
                .asset(stockAsset)
                .customer(customer)
                .size(initialStockAmount)
                .usableSize(initialStockAmount)
                .build();
        customerAssetRepository.save(customerStockAsset);

        // Create buy order for AAPL
        OrderCreateRequest buyRequest = new OrderCreateRequest(
                customer.getId(),
                appleAsset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(4),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse buyOrder = orderService.createOrder(buyRequest);

        // Create sell order for EREGL
        OrderCreateRequest sellRequest = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.valueOf(6),  // size
                BigDecimal.valueOf(10)  // price
        );
        OrderCreateResponse sellOrder = orderService.createOrder(sellRequest);

        // Verify funds were locked for buy order
        CustomerAsset fundsAfterOrders = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        BigDecimal buyOrderAmount = buyRequest.price().multiply(buyRequest.size());
        BigDecimal expectedUsableFunds = initialBalance.subtract(buyOrderAmount);
        Assertions.assertEquals(0, fundsAfterOrders.getUsableSize().compareTo(expectedUsableFunds));

        // Verify assets were locked for sell order
        CustomerAsset stockAfterOrders = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();
        BigDecimal expectedUsableStock = initialStockAmount.subtract(sellRequest.size());
        Assertions.assertEquals(0, stockAfterOrders.getUsableSize().compareTo(expectedUsableStock));

        // Cancel both orders
        orderService.deleteOrder(buyOrder.id());
        orderService.deleteOrder(sellOrder.id());

        // Verify funds were fully released
        CustomerAsset fundsAfterCancellations = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), tryAsset.getTicker()).get();
        Assertions.assertEquals(0, fundsAfterCancellations.getUsableSize().compareTo(initialBalance));

        // Verify assets were fully returned
        CustomerAsset stockAfterCancellations = customerAssetRepository.findByCustomerIdAndAssetTicker(
                customer.getId(), stockAsset.getTicker()).get();
        Assertions.assertEquals(0, stockAfterCancellations.getUsableSize().compareTo(initialStockAmount));
    }


    @Test
    void whenDeleteNonPendingOrderThenThrowOrderStatusException() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                .asset(stockAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest request = new OrderCreateRequest(customer.getId(), stockAsset.getTicker(),
                OrderSide.BUY, BigDecimal.ONE, BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);
        orderService.deleteOrder(createdOrder.id());

        Assertions.assertThrows(OrderStatusException.class, () -> orderService.deleteOrder(createdOrder.id()));
    }

    @Test
    void whenDeleteOrderWithNonExistentCustomerAssetThenThrowRuntimeException() throws InterruptedException {
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                .asset(stockAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialUsableAsset)
                .build();
        customerAssetRepository.save(customerAsset);

        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.ONE,
                BigDecimal.valueOf(10));
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        customerAssetRepository.deleteAll();

        Assertions.assertThrows(RuntimeException.class, () -> orderService.deleteOrder(createdOrder.id()));
    }


    @Test
    void whenMatchBuyOrderThenFundsReducedAndAssetIncreased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                stockAsset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(10), // price
                BigDecimal.valueOf(5)   // size
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        // Match the order
        OrderMatchResponse matchedOrder = orderService.matchOrder(createdOrder.id());

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
                customer.getId(), stockAsset.getTicker()).get();
        Assertions.assertEquals(0, customerAssetAfterMatch.getSize().compareTo(request.size()));
    }

    @Test
    void whenMatchSellOrderThenAssetReducedAndFundsIncreased() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                .asset(stockAsset)
                .customer(customer)
                .size(initialAssetAmount)
                .usableSize(initialAssetAmount)
                .build();
        customerAssetRepository.save(customerAsset);

        // Create order
        OrderCreateRequest request = new OrderCreateRequest(
                customer.getId(),
                stockAsset.getTicker(),
                OrderSide.SELL,
                BigDecimal.valueOf(10), // price
                BigDecimal.valueOf(5)   // size
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        // Match the order
        OrderMatchResponse matchedOrder = orderService.matchOrder(createdOrder.id());

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
                customer.getId(), stockAsset.getTicker()).get();
        BigDecimal expectedAssetAmount = initialAssetAmount.subtract(request.size());
        Assertions.assertEquals(0, customerAssetAfterMatch.getSize().compareTo(expectedAssetAmount));
    }

    @Test
    void whenMatchNonPendingOrderThenThrowOrderStatusException() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

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
                stockAsset.getTicker(),
                OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(5)
        );
        OrderCreateResponse createdOrder = orderService.createOrder(request);

        OrderMatchResponse matchedOrder = orderService.matchOrder(createdOrder.id());

        // Try to match it again
        Assertions.assertThrows(OrderStatusException.class, () -> orderService.matchOrder(createdOrder.id()));
    }

    @Test
    void whenMatchOrderWithNonExistentFundsThenThrowCustomerAssetNotFoundException() throws InterruptedException {
        // Setup assets
        Asset tryAsset = new Asset("TRY", "Turkish Lira");
        Asset stockAsset = new Asset("EREGL", "Eregli Demir ve Celik Fabrikalari T.A.S");
        assetRepository.save(tryAsset);
        assetRepository.save(stockAsset);

        // Setup customer
        Customer customer = new Customer();
        customer.setUsername("John Doe");
        userRepository.save(customer);

        // Create order directly without setting up customer funds
        Order order = Order.builder()
                .customer(customer)
                .asset(stockAsset)
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(5))
                .price(BigDecimal.valueOf(10))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        // Try to match the order
        Order finalOrder = order;

        Assertions.assertThrows(CustomerAssetNotFoundException.class, () -> orderService.matchOrder(finalOrder.getId()));
    }

}