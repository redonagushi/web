//Request body për register (emri, mbiemri, nrTel, datëlindja, email, password, confirm).
package com.example.platform.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {

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
    @Pattern(regexp = "^\\+35569\\d{7}$") // +35569xxxxxxx
    private String nrTel;

    @NotNull
    @Past
    private LocalDate datelindja;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!]).{8,}$")
    private String password;

    @NotBlank
    private String confirmPassword;
}
