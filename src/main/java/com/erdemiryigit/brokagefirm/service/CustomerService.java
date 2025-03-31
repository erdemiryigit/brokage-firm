package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.entity.User;
import com.erdemiryigit.brokagefirm.exception.UserNotFoundException;
import com.erdemiryigit.brokagefirm.repository.CustomerRepository;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public String getUsernameById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + customerId));
        if (customer == null) return "";

        return userRepository.findById(customerId)
                .map(User::getUsername)
                .orElse("");
    }

    public String getUsernameByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return "";

        return getUsernameById(order.getCustomer().getId());
    }
}