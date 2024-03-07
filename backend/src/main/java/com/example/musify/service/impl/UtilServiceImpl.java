package com.example.musify.service.impl;

import com.example.musify.entity.User;
import com.example.musify.repository.UserRepository;
import com.example.musify.service.IUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilServiceImpl implements IUtilService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).get();
    }
}
