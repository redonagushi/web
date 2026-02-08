//Request për admin që ndryshon një user tjetër (role, email, nrTel, etj.).
package com.example.platform.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{1,20}$")
    private String emri;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{1,20}$")
    private String atesia;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z]{1,20}$")
    private String mbiemri;

    @NotBlank
    @Pattern(regexp = "^\\+35569\\d{7}$")
    private String nrTel;

    @NotNull
    @Past
    private LocalDate datelindja;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String role; // USER ose ADMIN
}

