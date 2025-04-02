package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderGetResponse;
import com.erdemiryigit.brokagefirm.specification.CustomerAssetSearchCriteria;
import com.erdemiryigit.brokagefirm.specification.OrderSearchCriteria;
import com.erdemiryigit.brokagefirm.dto.request.OrderCreateRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderCreateResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderDeleteResponse;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public interface OrderService {

    @Transactional(readOnly = true)
    List<OrderGetResponse> searchOrders(OrderSearchCriteria criteria);

    @Transactional(readOnly = true)
    OrderGetResponse getOrderById(UUID orderId);

    OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) throws InterruptedException;

    OrderDeleteResponse deleteOrder(UUID orderId) throws InterruptedException;

    OrderMatchResponse matchOrder(UUID orderId) throws InterruptedException;

    @Transactional(readOnly = true)
    List<CustomerAssetGetResponse> searchAssets(CustomerAssetSearchCriteria criteria);
}
