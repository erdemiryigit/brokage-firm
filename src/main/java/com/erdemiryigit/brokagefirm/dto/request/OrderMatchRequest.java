package com.erdemiryigit.brokagefirm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderMatchRequest(
        @NotNull(message = "Order ID cannot be null")
        UUID id
) {
}