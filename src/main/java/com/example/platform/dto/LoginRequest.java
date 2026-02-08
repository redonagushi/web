//Request body për login (email/telefon + password).

package com.example.platform.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String emailOrPhone;
    private String password;
}

