//Merr user-in aktual nga SecurityContext (p.sh. getCurrentUser()).
//
//Të kursen të mos përsërisësh kodin në çdo controller.
package com.example.platform.service;

import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}
