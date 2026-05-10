package com.example.platform.unit;

import com.example.platform.dto.AuthResponse;
import com.example.platform.dto.LoginRequest;
import com.example.platform.dto.RegisterRequest;
import com.example.platform.entity.Role;
import com.example.platform.entity.User;
import com.example.platform.exception.EmailAlreadyExistsException;
import com.example.platform.repository.UserRepository;
import com.example.platform.security.JwtService;
import com.example.platform.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit teste per AuthService (register dhe login).
 * Perdor Mockito per te izoluar logjiken pa DB te vertete.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private AuthService authService;

    private RegisterRequest validReq;
    private User testUser;

    @BeforeEach
    void setUp() {
        validReq = new RegisterRequest();
        validReq.setEmri("Redon");
        validReq.setAtesia("Hysni");
        validReq.setMbiemri("Agushi");
        validReq.setNrTel("+355691234567");
        validReq.setDatelindja(LocalDate.of(2000, 1, 1));
        validReq.setEmail("redon@test.com");
        validReq.setPassword("Test@1234");
        validReq.setConfirmPassword("Test@1234");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmri("Redon");
        testUser.setMbiemri("Agushi");
        testUser.setEmail("redon@test.com");
        testUser.setPassword("$2a$hashedpassword");
        testUser.setRole(Role.USER);
    }

    // ─── REGISTER ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register() – te dhena valide → ruhet user-i")
    void register_withValidData_savesUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNrTel(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");

        authService.register(validReq);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("register() – email ekzistues → hedh EmailAlreadyExistsException")
    void register_existingEmail_throwsEmailAlreadyExistsException() {
        when(userRepository.existsByEmail("redon@test.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(validReq));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register() – nr tel ekzistues → hedh ResponseStatusException (409)")
    void register_existingPhone_throwsConflict() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNrTel("+355691234567")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> authService.register(validReq));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register() – password nuk perputhet → hedh ResponseStatusException (400)")
    void register_passwordMismatch_throwsBadRequest() {
        validReq.setConfirmPassword("DifferentPassword@1");

        assertThrows(ResponseStatusException.class,
                () -> authService.register(validReq));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register() – password i koduar para ruajtjes")
    void register_passwordIsEncoded() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNrTel(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Test@1234")).thenReturn("$2a$encoded");

        authService.register(validReq);

        verify(passwordEncoder, times(1)).encode("Test@1234");
    }

    // ─── LOGIN ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login() – kredenciale valide → kthen AuthResponse me token")
    void login_validCredentials_returnsAuthResponse() {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("redon@test.com");
        req.setPassword("Test@1234");

        when(userRepository.findByEmail("redon@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Test@1234", "$2a$hashedpassword")).thenReturn(true);
        when(jwtService.generateToken("redon@test.com")).thenReturn("jwt.token.value");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("jwt.token.value", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("redon@test.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("login() – password gabim → hedh BadCredentialsException")
    void login_wrongPassword_throwsBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("redon@test.com");
        req.setPassword("WrongPass@1");

        when(userRepository.findByEmail("redon@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("login() – email jo ekzistues → hedh BadCredentialsException")
    void login_nonExistentEmail_throwsBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("nobody@nowhere.com");
        req.setPassword("SomePass@1");

        when(userRepository.findByEmail("nobody@nowhere.com")).thenReturn(Optional.empty());
        when(userRepository.findByNrTel("nobody@nowhere.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("login() – login me nr tel → funksionon")
    void login_withPhoneNumber_returnsToken() {
        LoginRequest req = new LoginRequest();
        req.setEmailOrPhone("+355691234567");
        req.setPassword("Test@1234");

        when(userRepository.findByEmail("+355691234567")).thenReturn(Optional.empty());
        when(userRepository.findByNrTel("+355691234567")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Test@1234", "$2a$hashedpassword")).thenReturn(true);
        when(jwtService.generateToken("redon@test.com")).thenReturn("jwt.phone.token");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("jwt.phone.token", response.getToken());
    }
}