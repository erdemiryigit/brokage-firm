package com.erdemiryigit.brokagefirm.config;

import com.erdemiryigit.brokagefirm.entity.User;
import com.erdemiryigit.brokagefirm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class PasswordInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing user passwords...");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            // Set password to be the same as username
            String rawPassword = user.getUsername();
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);

            log.info("Updated password for user: {}", user.getUsername());
        }

        userRepository.saveAll(users);
        log.info("Password initialization completed");
    }
}