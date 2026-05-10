package com.example.platform.integration;

import com.example.platform.dto.LoginRequest;
import com.example.platform.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste integrimi per AuthController.
 * Perdor MockMvc dhe H2 in-memory per te testuar endpoint-et REST
 * te autentikimit pa nje server te vertete.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // ─── REGISTER ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/register – te dhena valide → 200 OK")
    void register_validData_returns200() throws Exception {
        RegisterRequest req = buildRegister("integ@test.com", "+355693333333");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/register – email ekzistues (admin) → 409 Conflict")
    void register_existingEmail_returns409() throws Exception {
        RegisterRequest req = buildRegister("admin@email.com", "+355694444444");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register – fusha boshe → 400 Bad Request")
    void register_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register – password nuk perputhet → 400 Bad Request")
    void register_passwordMismatch_returns400() throws Exception {
        RegisterRequest req = buildRegister("new@test.com", "+355695555555");
        req.setConfirmPassword("DifferentPass@9");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register – nr tel invalid (format gabim) → 400 Bad Request")
    void register_invalidPhone_returns400() throws Exception {
        RegisterRequest req = buildRegister("phone@test.com", "0682345678"); // nuk fillon me +35569

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login – admin login → 200 me token dhe role ADMIN")
    void login_asAdmin_returns200WithToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("admin@email.com");
        req.setPassword("Admin@123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("admin@email.com"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.user.password").doesNotExist()); // password NUK duhet kthyer
    }

    @Test
    @DisplayName("POST /api/auth/login – password gabim → 401 Unauthorized")
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("admin@email.com");
        req.setPassword("WrongPassword@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login – email jo ekzistues → 401 Unauthorized")
    void login_nonExistentUser_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("nobody@nowhere.com");
        req.setPassword("SomePass@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login – login me nr tel → 200 OK")
    void login_withPhoneNumber_returns200() throws Exception {
        // regjistro nje user me nr tel
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                buildRegister("phone.user@test.com", "+355696666666"))))
                .andExpect(status().isOk());

        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("+355696666666");
        req.setPassword("Test@1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────

    private RegisterRequest buildRegister(String email, String phone) {
        RegisterRequest req = new RegisterRequest();
        req.setEmri("Testi");
        req.setAtesia("Testimit");
        req.setMbiemri("Testimir");
        req.setNrTel(phone);
        req.setDatelindja(LocalDate.of(1998, 6, 15));
        req.setEmail(email);
        req.setPassword("Test@1234");
        req.setConfirmPassword("Test@1234");
        return req;
    }
}