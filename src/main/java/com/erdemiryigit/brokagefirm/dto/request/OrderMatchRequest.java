package com.erdemiryigit.brokagefirm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderMatchRequest(
        @NotNull(message = "Order ID cannot be null")
        Long id,

        @NotNull(message = "Username cannot be null")
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotNull(message = "Password cannot be null")
        @NotBlank(message = "Password cannot be blank")
        String password
) {
}