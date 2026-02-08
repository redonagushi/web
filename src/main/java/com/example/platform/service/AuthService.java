//Logjika e login/register:
//
//register → ruan user (me password hashed)
//
//login → verifikon password → gjeneron JWT
package com.example.platform.service;

import com.example.platform.dto.*;
import com.example.platform.entity.*;
import com.example.platform.exception.EmailAlreadyExistsException;
import com.example.platform.repository.UserRepository;
import com.example.platform.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password nuk përputhet");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new EmailAlreadyExistsException("Ky email është i regjistruar. Provo Login.");
        }

        if (userRepository.existsByNrTel(req.getNrTel())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nr Tel ekziston");
        }

        User user = new User();
        user.setEmri(req.getEmri());
        user.setAtesia(req.getAtesia());
        user.setMbiemri(req.getMbiemri());
        user.setNrTel(req.getNrTel());
        user.setDatelindja(req.getDatelindja());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository
                .findByEmail(req.getEmailOrPhone())
                .or(() -> userRepository.findByNrTel(req.getEmailOrPhone()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
