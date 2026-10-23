package com.societyhub.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder().encodeToString("a".repeat(48).getBytes());
    private static final String OTHER_SECRET = Base64.getEncoder().encodeToString("b".repeat(48).getBytes());
    private static final long ONE_HOUR = 3_600_000L;

    private final UserDetails alice = User.withUsername("alice@college.edu").password("x").authorities("ROLE_USER").build();
    private final UserDetails bob = User.withUsername("bob@college.edu").password("x").authorities("ROLE_USER").build();

    private JwtService service(String secret, long expirationMs) {
        JwtService s = new JwtService();
        ReflectionTestUtils.setField(s, "secretKey", secret);
        ReflectionTestUtils.setField(s, "jwtExpiration", expirationMs);
        s.validateConfiguration();
        return s;
    }

    // ---- tokens ----

    @Test
    void aFreshTokenIsValidAndCarriesTheUsername() {
        JwtService jwt = service(SECRET, ONE_HOUR);

        String token = jwt.generateToken(alice);

        assertEquals("alice@college.edu", jwt.extractUsername(token));
        assertTrue(jwt.isTokenValid(token, alice));
    }

    @Test
    void aTokenIsNotValidForAnotherUser() {
        JwtService jwt = service(SECRET, ONE_HOUR);

        assertFalse(jwt.isTokenValid(jwt.generateToken(alice), bob));
    }

    @Test
    void anExpiredTokenIsRejectedWithoutAnException() {
        JwtService jwt = service(SECRET, ONE_HOUR);
        ReflectionTestUtils.setField(jwt, "jwtExpiration", -1000L); // already expired when created

        String token = jwt.generateToken(alice);

        assertFalse(jwt.isTokenValid(token, alice));
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejected() {
        String forged = service(OTHER_SECRET, ONE_HOUR).generateToken(alice);

        assertFalse(service(SECRET, ONE_HOUR).isTokenValid(forged, alice));
    }

    @Test
    void aTamperedTokenIsRejected() {
        JwtService jwt = service(SECRET, ONE_HOUR);
        String[] parts = jwt.generateToken(alice).split("\\.");
        char first = parts[1].charAt(0);
        String tamperedPayload = (first == 'x' ? 'y' : 'x') + parts[1].substring(1);

        assertFalse(jwt.isTokenValid(parts[0] + "." + tamperedPayload + "." + parts[2], alice));
    }

    @Test
    void garbageIsRejected() {
        assertFalse(service(SECRET, ONE_HOUR).isTokenValid("not-a-token", alice));
    }

    // ---- startup checks ----

    @Test
    void startupFailsWhenTheSecretIsMissing() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service("", ONE_HOUR));

        assertTrue(ex.getMessage().contains("JWT_SECRET"));
    }

    @Test
    void startupFailsWhenTheSecretIsTooShort() {
        String shortSecret = Base64.getEncoder().encodeToString("short-key-16-byte".getBytes());

        assertThrows(IllegalStateException.class, () -> service(shortSecret, ONE_HOUR));
    }

    @Test
    void startupFailsWhenTheSecretIsNotBase64() {
        assertThrows(IllegalStateException.class, () -> service("this is not base64 !!!", ONE_HOUR));
    }

    @Test
    void startupFailsWhenTheLifetimeIsNotPositive() {
        assertThrows(IllegalStateException.class, () -> service(SECRET, 0));
    }
}