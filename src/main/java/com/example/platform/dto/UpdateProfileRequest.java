//Request për user-in që ndryshon profilin e vet.
package com.example.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    @NotBlank
    @Pattern(regexp="^[A-Za-z]{1,20}$") private String emri;
    @NotBlank @Pattern(regexp="^[A-Za-z]{1,20}$") private String atesia;
    @NotBlank @Pattern(regexp="^[A-Za-z]{1,20}$") private String mbiemri;
    @NotBlank @Pattern(regexp="^\\+35569\\d{7}$") private String nrTel;
    @NotNull
    @Past
    private LocalDate datelindja;
}

