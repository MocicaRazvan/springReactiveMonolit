package com.example.wellness.bootstrap;

import com.example.wellness.enums.Role;
import com.example.wellness.models.user.UserCustom;
import com.example.wellness.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadUsers() {
        return args -> userRepository.count()
                .filter(count -> count == 0)
                .flatMap(isEmpty -> userRepository.save(
                        UserCustom.builder()
                                .firstName("Razvan")
                                .lastName("Mocica")
                                .email("razvanmocica@gmail.com")
                                .password(passwordEncoder.encode("1234"))
                                .role(Role.ROLE_ADMIN)
                                .build()
                ))
                .subscribe(
                        user -> System.out.println("User saved: " + user),
                        error -> System.err.println("Failed to save user: " + error),
                        () -> System.out.println("No users needed to be added.")
                );
    }
}
