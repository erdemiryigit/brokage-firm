package com.erdemiryigit.brokagefirm.service;

import com.erdemiryigit.brokagefirm.entity.User;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PasswordHashingTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testPasswordHashing() {
        String rawPassword = "admin";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        // Verify that the raw password matches the encoded password
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        User user = userRepository.findByUsername("admin").orElseThrow(() -> new RuntimeException("User not found"));
        assertTrue(encoder.matches(rawPassword, user.getPassword()));
    }
}