package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.application.core.domains.JwtClaims;
import br.com.jtech.tasklist.application.ports.output.JwtTokenOutputGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenAdapter implements JwtTokenOutputGateway {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_TYPE = "typ";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final SecretKey signingKey;
    private final String issuer;
    private final long accessExpirationMinutes;
    private final long refreshExpirationMinutes;

    public JwtTokenAdapter(@Value("${jwt.secret}") String secret,
                           @Value("${jwt.issuer}") String issuer,
                           @Value("${jwt.access-expiration-minutes}") long accessExpirationMinutes,
                           @Value("${jwt.refresh-expiration-minutes}") long refreshExpirationMinutes) {
        byte[] keyBytes = decodeSecret(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationMinutes = refreshExpirationMinutes;
    }

    private byte[] decodeSecret(String secret) {
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (IllegalArgumentException ignored) {
        }
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String generateAccessToken(UUID userId, String email) {
        return buildToken(userId, email, TYPE_ACCESS, accessExpirationMinutes);
    }

    @Override
    public String generateRefreshToken(UUID userId, String email) {
        return buildToken(userId, email, TYPE_REFRESH, refreshExpirationMinutes);
    }

    @Override
    public long getAccessExpirationSeconds() {
        return accessExpirationMinutes * 60L;
    }

    @Override
    public JwtClaims parseAccessToken(String token) {
        return parse(token, TYPE_ACCESS);
    }

    @Override
    public JwtClaims parseRefreshToken(String token) {
        return parse(token, TYPE_REFRESH);
    }

    private String buildToken(UUID userId, String email, String type, long expirationMinutes) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_TYPE, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    private JwtClaims parse(String token, String expectedType) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Object type = claims.get(CLAIM_TYPE);
        if (type == null || !expectedType.equals(type.toString())) {
            throw new IllegalArgumentException("Tipo de token inválido");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get(CLAIM_EMAIL, String.class);

        return JwtClaims.builder()
                .userId(userId)
                .email(email)
                .build();
    }
}
