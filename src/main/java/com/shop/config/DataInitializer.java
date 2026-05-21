package com.shop.config;

import com.shop.entity.User;
import com.shop.entity.UserRole;
import com.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUser("admin", "admin123", "admin@shop.com", Set.of(UserRole.ADMIN, UserRole.USER));
            seedUser("user", "user123", "user@shop.com", Set.of(UserRole.USER));
        }
    }

    private void seedUser(String username, String rawPassword, String email, Set<UserRole> roles) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(email)
                .enabled(true)
                .roles(roles)
                .build();
        userRepository.save(user);
    }
}
