package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.dto.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.dto.OrderSearchCriteria;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.request.OrderMatchRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface OrderService {

    @Transactional(readOnly = true)
    List<Order> searchOrders(OrderSearchCriteria criteria);

    List<Order> getAllOrders();

    OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) throws InterruptedException;

    Order deleteOrder(Long orderId) throws InterruptedException;

    // todo kod tekrarini duzelt
    OrderMatchResponse matchOrder(OrderMatchRequest orderMatchRequest) throws InterruptedException;

    @Transactional(readOnly = true)
    List<CustomerAsset> searchAssets(CustomerAssetSearchCriteria criteria);
}
