//Response nga login: zakonisht { token, user } ose { token, role, expiresIn }.
package com.example.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
}
