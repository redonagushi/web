//Merr user-in nga DB dhe e kthen në formatin që do Spring Security (UserDetails).
//
//Përdoret gjatë autentikimit (login / token).
package com.example.platform.security;

import com.example.platform.entity.User;
import com.example.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrPhone)
                .or(() -> userRepository.findByNrTel(emailOrPhone))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // username brenda Spring (mund të jetë email)
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
