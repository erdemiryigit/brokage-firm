package com.erdemiryigit.brokagefirm.controller;

import com.erdemiryigit.brokagefirm.dto.request.OrderMatchRequest;
import com.erdemiryigit.brokagefirm.dto.response.OrderMatchResponse;
import com.erdemiryigit.brokagefirm.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final OrderService orderService;

    //@PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Match Order", description = "Match a pending order.")
    @PostMapping
    public ResponseEntity<OrderMatchResponse> matchOrder(@RequestBody @Valid OrderMatchRequest orderMatchRequest) {
        log.info("Admin matching order: {}", orderMatchRequest);
        try {
            return ResponseEntity.ok(orderService.matchOrder(orderMatchRequest));
        } catch (InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}