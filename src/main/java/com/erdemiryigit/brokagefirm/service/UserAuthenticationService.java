package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.entity.Customer;
import com.erdemiryigit.brokagefirm.entity.Order;
import com.erdemiryigit.brokagefirm.entity.User;
import com.erdemiryigit.brokagefirm.exception.OrderNotFoundException;
import com.erdemiryigit.brokagefirm.model.CustomUserDetails;
import com.erdemiryigit.brokagefirm.repository.OrderRepository;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order NOT found"));
        Customer customer = order.getCustomer();
        if (customer == null) {
            throw new UsernameNotFoundException("Customer not found");
        }
        return loadUserByUsername(customer.getName());
    }

}
