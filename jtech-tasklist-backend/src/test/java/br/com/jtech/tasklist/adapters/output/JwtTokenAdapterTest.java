package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.application.core.domains.JwtClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenAdapterTest {

    private static final String SECRET = "dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=";
    private static final String ISSUER = "jtech-tasklist-test";

    private JwtTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenAdapter(SECRET, ISSUER, 60, 1440);
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        UUID userId = UUID.randomUUID();
        String token = adapter.generateAccessToken(userId, "a@a.com");
        JwtClaims claims = adapter.parseAccessToken(token);
        assertThat(claims.getUserId()).isEqualTo(userId);
        assertThat(claims.getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void shouldGenerateAndParseRefreshToken() {
        UUID userId = UUID.randomUUID();
        String token = adapter.generateRefreshToken(userId, "b@b.com");
        JwtClaims claims = adapter.parseRefreshToken(token);
        assertThat(claims.getUserId()).isEqualTo(userId);
        assertThat(claims.getEmail()).isEqualTo("b@b.com");
    }

    @Test
    void shouldRejectAccessTokenWhenParsingAsRefresh() {
        String token = adapter.generateAccessToken(UUID.randomUUID(), "x@x.com");
        assertThatThrownBy(() -> adapter.parseRefreshToken(token))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldExposeAccessExpirationSeconds() {
        assertThat(adapter.getAccessExpirationSeconds()).isEqualTo(3600L);
    }

    @Test
    void shouldAcceptRawSecretFallback() {
        JwtTokenAdapter raw = new JwtTokenAdapter("raw-secret-with-at-least-32-characters!!", ISSUER, 1, 1);
        String token = raw.generateAccessToken(UUID.randomUUID(), "z@z.com");
        assertThat(raw.parseAccessToken(token)).isNotNull();
    }
}
