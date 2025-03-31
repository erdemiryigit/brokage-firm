package com.erdemiryigit.brokagefirm.dto.request;

import jakarta.validation.constraints.NotNull;

public record OrderDeleteRequest(
        @NotNull(message = "Order ID cannot be null")
        Long orderId
) {
}