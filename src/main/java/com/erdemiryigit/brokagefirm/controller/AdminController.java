package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.config.annotations.IsAdmin;
import com.erdemiryigit.brokagefirm.dto.request.OrderMatchRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
@IsAdmin
public class AdminController {

    private final OrderService orderService;

    @Operation(summary = "Match Order", description = "Match a pending order.")
    @PostMapping
    public ResponseEntity<OrderMatchResponse> matchOrder(@RequestBody @Valid OrderMatchRequest orderMatchRequest) throws InterruptedException {
        return ResponseEntity.ok(orderService.matchOrder(orderMatchRequest));
    }
}
