package com.example.musify.init;

import com.example.musify.entity.Role;
import com.example.musify.entity.User;
import com.example.musify.enumeration.ERole;
import com.example.musify.repository.RoleRepository;
import com.example.musify.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        Optional<Role> existingUserRole = roleRepository.findByName(ERole.ROLE_USER);
        Optional<Role> existingAdminRole = roleRepository.findByName(ERole.ROLE_ADMIN);

        if (existingUserRole.isEmpty()) {
            Role userRole = roleRepository.save(Role.builder().name(ERole.ROLE_USER).build());
            existingUserRole = Optional.of(userRole);
        }

        if (existingAdminRole.isEmpty()) {
            Role adminRole = roleRepository.save(Role.builder().name(ERole.ROLE_ADMIN).build());
            existingAdminRole = Optional.of(adminRole);
        }

        if (userRepository.findByUsername("reze").isEmpty()) {
            User admin = User.builder()
                    .username("reze")
                    .email("reze@csm.com")
                    .password(passwordEncoder.encode("mgla"))
                    .roles(Set.of(existingAdminRole.get(), existingUserRole.get()))
                    .build();

            userRepository.save(admin);
        }
    }
}
