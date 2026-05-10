package com.example.platform.unit;

import com.example.platform.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit teste per JwtService.
 * Testohet gjenerimi, leximi dhe validimi i token-it JWT.
 */
class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "test_secret_key_must_be_at_least_64_characters_long_for_HS256_algorithm_ok";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 ore

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken() – kthen token jo-null dhe jo-bosh")
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken("test@example.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("extractSubject() – kthen emailin e sakte nga token")
    void extractSubject_returnsCorrectEmail() {
        String email = "user@platform.al";
        String token = jwtService.generateToken(email);

        assertEquals(email, jwtService.extractSubject(token));
    }

    @Test
    @DisplayName("Token i ndryshuar – hedh JwtException")
    void tamperedToken_throwsException() {
        String token = jwtService.generateToken("test@example.com");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThrows(Exception.class, () -> jwtService.extractSubject(tampered));
    }

    @Test
    @DisplayName("Token i skaduar – hedh JwtException")
    void expiredToken_throwsJwtException() {
        // vendos skadim negativ → token skadon menjëherë
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken("expired@example.com");

        assertThrows(JwtException.class, () -> jwtService.extractSubject(token));
    }

    @Test
    @DisplayName("Token i gjeneruar – permban 3 pjese (header.payload.signature)")
    void generateToken_hasThreeParts() {
        String token = jwtService.generateToken("user@test.com");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT duhet te kete 3 pjese: header.payload.signature");
    }
}