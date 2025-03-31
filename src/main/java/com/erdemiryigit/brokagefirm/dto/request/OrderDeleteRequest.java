package com.erdemiryigit.brokagefirm.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderDeleteRequest(
        @NotNull(message = "Order ID cannot be null")
        UUID orderId
) {
}