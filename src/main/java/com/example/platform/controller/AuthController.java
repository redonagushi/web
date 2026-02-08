//API për register dhe login.
//
//Login kthen token (JWT) + info të user-it (role, etj.).
//
//Register krijon user në DB dhe bën validime (email unique).
package com.example.platform.controller;

import com.example.platform.dto.AuthResponse;
import com.example.platform.dto.LoginRequest;
import com.example.platform.dto.RegisterRequest;
import com.example.platform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
              return ResponseEntity.ok(authService.login(request));
    }



}

