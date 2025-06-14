package com.erdemiryigit.brokagefirm.service.impl;

import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.enums.OrderSide;
import com.erdemiryigit.brokagefirm.enums.OrderStatus;
import com.erdemiryigit.brokagefirm.exception.OrderSideUnknownException;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.specification.OrderSearchCriteria;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.entity.Asset;
import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.exception.AssetNotFoundException;
import com.erdemiryigit.brokagefirm.exception.CustomerAssetInsufficientException;
import com.erdemiryigit.brokagefirm.exception.CustomerAssetNotFoundException;
import com.erdemiryigit.brokagefirm.exception.CustomerNotFoundException;
import com.erdemiryigit.brokagefirm.exception.OrderInterruptedException;
import com.erdemiryigit.brokagefirm.exception.OrderNotFoundException;
import com.erdemiryigit.brokagefirm.exception.OrderStatusException;
import com.erdemiryigit.brokagefirm.repository.AssetRepository;
import com.erdemiryigit.brokagefirm.repository.CustomerAssetRepository;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.service.OrderService;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSpecification;
import com.erdemiryigit.brokagefirm.specification.OrderSpecification;
import com.erdemiryigit.brokagefirm.util.CustomerAssetMapper;
import com.erdemiryigit.brokagefirm.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

//todo customer kendi orderini cancel edebilir, admin herkesinkini edebilir

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final CustomerAssetRepository customerAssetRepository;

    private final LockRegistry lockRegistry;

    private static final String TRY = "TRY";
    private final OrderMapper orderMapper;
    private final CustomerAssetMapper customerAssetMapper;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.DEFAULT)
    public OrderGetResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id: " + orderId + " NOT found!"));

        return orderMapper.toOrderGetResponse(order);
    }

    @Override
    @Transactional
    public OrderDeleteResponse deleteOrder(UUID orderId) throws InterruptedException {
        Order finalOrder;
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with id: " + orderId + " NOT found!"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderStatusException("Only pending orders can be cancelled, this order is already " + order.getStatus());
        }

        Asset tryAsset = assetRepository.findById(TRY).orElseThrow(() -> new AssetNotFoundException("Asset " + TRY + " NOT found!"));

        CustomerAsset customerAsset;
        String customerAssetTicker;
        BigDecimal amountToReturn;
        if (OrderSide.BUY == order.getOrderSide()) {
            // For BUY orders, return money to TRY balance
            customerAssetTicker = TRY;
            amountToReturn = order.getPrice().multiply(order.getSize());
        } else if (OrderSide.SELL == order.getOrderSide()) {
            customerAssetTicker = order.getAsset().getTicker();
            amountToReturn = order.getSize();
        } else {
            throw new OrderSideUnknownException("Unknown order side: " + order.getOrderSide());
        }
        customerAsset = customerAssetRepository.findByCustomerIdAndAssetTicker(
                        order.getCustomer().getId(), customerAssetTicker)
                .orElseThrow(() -> new CustomerAssetNotFoundException("Customer with id: " + order.getCustomer().getId() + " asset with ticker:" + customerAssetTicker + " NOT found!"));

        Lock customerAssetLock = lockRegistry.obtain(String.valueOf(customerAsset.getId()));
        try {
            if (!customerAssetLock.tryLock(10, TimeUnit.SECONDS)) {
                throw new OrderInterruptedException("Can't cancel your order please try again later!");
            }

            // Add back the total cost (price * size)
            customerAsset.setUsableSize(customerAsset.getUsableSize().add(amountToReturn));

            customerAssetRepository.save(customerAsset);
            order.setStatus(OrderStatus.CANCELLED);
            finalOrder = orderRepository.save(order);
        } finally {
            customerAssetLock.unlock();
        }

        return orderMapper.toOrderDeleteResponse(finalOrder);
    }

    @Override
    @Transactional
    // todo createorderresponse return komple trya all catchte responseun statusunu failure don
    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) throws InterruptedException {
        Order finalOrder;
        Asset tryAsset = assetRepository.findById(TRY).orElseThrow(() -> new AssetNotFoundException("Asset " + TRY + " NOT found!"));

        Asset asset = assetRepository.findById(orderCreateRequest.ticker())
                .orElseThrow(() -> new AssetNotFoundException("Asset " + orderCreateRequest.ticker() + " NOT found!"));

        OrderSide orderSide = orderCreateRequest.orderSide();

        Customer customer = (Customer) userRepository.findById(orderCreateRequest.customerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id:" + orderCreateRequest.customerId() + " NOT found!"));

        if (OrderSide.BUY == orderSide) {
            CustomerAsset customerFunds = customerAssetRepository.findByCustomerIdAndAssetTicker(
                            orderCreateRequest.customerId(), tryAsset.getTicker())
                    .orElseThrow(() -> new CustomerAssetNotFoundException("Customer asset" + tryAsset.getTicker() + " NOT found for customer id: " + orderCreateRequest.customerId()));
            Lock customerAssetLock = lockRegistry.obtain(String.valueOf(customerFunds.getId()));
            try {
                Boolean hasLock = customerAssetLock.tryLock(10, TimeUnit.SECONDS);

                if (!hasLock) {
                    throw new OrderInterruptedException("Can't process your order please try again later!");
                }

                BigDecimal amount = orderCreateRequest.price().multiply((orderCreateRequest.size()));

                if (amount.compareTo(customerFunds.getUsableSize()) > 0) {
                    throw new CustomerAssetInsufficientException("Customer with id:" + customer.getId()
                            + " does NOT have enough of " + customerFunds.getAsset().getTicker() + " asset!");
                }

                customerFunds.setUsableSize(customerFunds.getUsableSize().subtract(amount));

                customerAssetRepository.save(customerFunds);

                finalOrder = orderRepository.save(Order.builder()
                        .customer(customer)
                        .asset(asset)
                        .orderSide(orderCreateRequest.orderSide())
                        .size(orderCreateRequest.size())
                        .price(orderCreateRequest.price())
                        .status(OrderStatus.PENDING) // Orders should be created with PENDING status.
                        .createDate(LocalDateTime.now())
                        .build());
            } finally {
                customerAssetLock.unlock();
            }

        } else if (OrderSide.SELL == orderSide) {
            CustomerAsset customerAsset = customerAssetRepository.findByCustomerIdAndAssetTicker(
                            orderCreateRequest.customerId(), orderCreateRequest.ticker())
                    .orElseThrow(() -> new CustomerAssetNotFoundException(
                            "Customer asset " + orderCreateRequest.ticker() +
                                    " NOT found for customer id: " + orderCreateRequest.customerId()));

            Lock customerAssetLock = lockRegistry.obtain(String.valueOf(customerAsset.getId()));
            try {
                Boolean hasLock = customerAssetLock.tryLock(10, TimeUnit.SECONDS);

                if (!hasLock) {
                    throw new OrderInterruptedException("Can't create your order please try again later!");
                }
                BigDecimal size = orderCreateRequest.size();

                if (size.compareTo(customerAsset.getUsableSize()) > 0) {
                    throw new CustomerAssetInsufficientException("Customer with id:" + customer.getId()
                            + " does NOT have enough of " + customerAsset.getAsset().getTicker() + " asset!");
                }

                customerAsset.setUsableSize(customerAsset.getUsableSize().subtract(size));
                customerAssetRepository.save(customerAsset);

                finalOrder = orderRepository.save(Order.builder()
                        .customer(customer)
                        .asset(asset)
                        .orderSide(orderCreateRequest.orderSide())
                        .size(orderCreateRequest.size())
                        .price(orderCreateRequest.price())
                        .status(OrderStatus.PENDING) // Orders should be created with PENDING status.
                        .createDate(LocalDateTime.now())
                        .build());

            } finally {
                customerAssetLock.unlock();
            }
        } else {
            throw new OrderSideUnknownException("Unknown order side: " + orderCreateRequest.orderSide());
        }

        return orderMapper.toOrderCreateResponse(finalOrder);
    }

    @Override
    public OrderMatchResponse matchOrder(UUID orderId) throws InterruptedException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order to be matched NOT found!"));

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new OrderStatusException(
                    "Order to be matched has to be in PENDING status! Order with id " + orderId + " status is " + order.getStatus());
        }

        Asset tryAsset = assetRepository.findById(TRY).get();
        Asset stockAsset = assetRepository.findById(order.getAsset().getTicker())
                .orElseThrow(() -> new AssetNotFoundException("Asset with id:" + order.getAsset().getTicker() + " NOT found!"));

        Customer customer = (Customer) userRepository.findById(order.getCustomer().getId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id:" + order.getCustomer().getId() + " NOT found!"));

        OrderSide orderSide = order.getOrderSide();

        // parasini bulup sizeindan orderin amountu kadar dusecegiz, usabledan dusmustuk
        CustomerAsset customerFunds = customerAssetRepository.findByCustomerIdAndAssetTicker(
                        customer.getId(), tryAsset.getTicker())
                .orElseThrow(() -> new CustomerAssetNotFoundException("Customer with id:" + customer.getId() + " funds NOT found!"));

        // For BUY orders, we might need to create a new asset record for first-time purchases
        // For SELL orders, asset must already exist
        CustomerAsset customerAsset;
        if (order.getOrderSide() == OrderSide.BUY) {
            customerAsset = customerAssetRepository.findByCustomerIdAndAssetTicker(
                            customer.getId(), stockAsset.getTicker())
                    .orElseGet(() -> {
                        CustomerAsset newAsset = CustomerAsset.builder()
                                .customer(customer)
                                .asset(stockAsset)
                                .size(BigDecimal.ZERO)
                                .usableSize(BigDecimal.ZERO)
                                .build();
                        return customerAssetRepository.save(newAsset);
                    });
        } else {
            customerAsset = customerAssetRepository.findByCustomerIdAndAssetTicker(
                            customer.getId(), stockAsset.getTicker())
                    .orElseThrow(() -> new CustomerAssetNotFoundException("Customer asset " + stockAsset.getTicker() +
                            " NOT found for customer id: " + customer.getId()));
        }

        Lock lock1 = lockRegistry.obtain(String.valueOf(customerFunds.getId()));
        Lock lock2 = lockRegistry.obtain(String.valueOf(customerAsset.getId()));

        try {
            if (!lock1.tryLock(10, TimeUnit.SECONDS) || !lock2.tryLock(10, TimeUnit.SECONDS)) {
                throw new OrderInterruptedException("Can't process your order please try again later!");
            }

            BigDecimal orderAmount = order.getPrice().multiply(order.getSize());
            if (orderSide == OrderSide.BUY) {
                customerFunds.setSize(customerFunds.getSize().subtract(orderAmount)); // aliyorken fundstan cikartiyoruz assete ekliyoruz
                customerAsset.setSize(customerAsset.getSize().add(order.getSize()));
                customerAsset.setUsableSize(customerAsset.getUsableSize().add(order.getSize())); // usable size da artmali
            } else if (orderSide == OrderSide.SELL) {
                customerFunds.setSize(customerFunds.getSize().add(orderAmount)); // satiyorken fundsa ekliyoruz assetten cikartiyoruz
                customerAsset.setSize(customerAsset.getSize().subtract(order.getSize()));
                customerFunds.setUsableSize(customerFunds.getUsableSize().add(orderAmount)); // usable size da artmali
            } else {
                throw new OrderSideUnknownException("Unknown order side: " + order.getOrderSide());
            }

            customerAssetRepository.save(customerFunds);
            customerAssetRepository.save(customerAsset);

            // her sey okeyse orderin statusu matche cekilir ve savelenir
            order.setStatus(OrderStatus.MATCHED);
            Order orderSaved = orderRepository.save(order);
            return orderMapper.toOrderMatchResponse(orderSaved);
        } finally {
            // reverse release locks
            lock2.unlock();
            lock1.unlock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderGetResponse> searchOrders(OrderSearchCriteria criteria) {
        Specification<Order> spec = Specification.where(null);

        if (criteria.getCustomerId() != null) {
            spec = spec.and(OrderSpecification.withCustomerId(criteria.getCustomerId()));
        }

        if (criteria.getAssetName() != null) {
            spec = spec.and(OrderSpecification.withAssetName(criteria.getAssetName()));
        }

        if (criteria.getOrderSide() != null) {
            spec = spec.and(OrderSpecification.withOrderSide(criteria.getOrderSide()));
        }

        if (criteria.getStatus() != null) {
            spec = spec.and(OrderSpecification.withStatus(criteria.getStatus()));
        }

        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            spec = spec.and(OrderSpecification.withCreateDateBetween(criteria.getStartDate(), criteria.getEndDate()));
        }
        if (criteria.getPrice() != null) {
            spec = spec.and(OrderSpecification.withPriceEquals(criteria.getPrice()));
        }

        if (criteria.getMinPrice() != null) {
            spec = spec.and(OrderSpecification.withPriceGreaterThan(criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null) {
            spec = spec.and(OrderSpecification.withPriceLessThan(criteria.getMaxPrice()));
        }

        if (criteria.getSize() != null) {
            spec = spec.and(OrderSpecification.withSizeEquals(criteria.getSize()));
        }

        if (criteria.getMinSize() != null) {
            spec = spec.and(OrderSpecification.withSizeGreaterThan(criteria.getMinSize()));
        }

        if (criteria.getMaxSize() != null) {
            spec = spec.and(OrderSpecification.withSizeLessThan(criteria.getMaxSize()));
        }

        return orderMapper.toOrderGetResponseList(orderRepository.findAll(spec));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAssetGetResponse> searchAssets(CustomerAssetSearchCriteria criteria) {
        Specification<CustomerAsset> spec = Specification.where(null);

        if (criteria.getCustomerId() != null) {
            spec = spec.and(CustomerAssetSpecification.withCustomerId(criteria.getCustomerId()));
        }

        if (criteria.getTicker() != null) {
            spec = spec.and(CustomerAssetSpecification.withAssetId(criteria.getTicker()));
        }

        if (criteria.getUsableSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withUsableSizeEquals(criteria.getUsableSize()));
        }

        if (criteria.getMinUsableSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withUsableSizeGreaterThan(criteria.getMinUsableSize()));
        }

        if (criteria.getMaxUsableSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withUsableSizeLessThan(criteria.getMaxUsableSize()));
        }

        if (criteria.getSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withSizeEquals(criteria.getSize()));
        }

        if (criteria.getMinSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withSizeGreaterThan(criteria.getMinSize()));
        }

        if (criteria.getMaxSize() != null) {
            spec = spec.and(CustomerAssetSpecification.withSizeLessThan(criteria.getMaxSize()));
        }

        return customerAssetMapper.toCustomerAssetGetResponseList(customerAssetRepository.findAll(spec));
    }
}
