package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.JwtClaims;

import java.util.UUID;

public interface JwtTokenOutputGateway {

    String generateAccessToken(UUID userId, String email);

    String generateRefreshToken(UUID userId, String email);

    long getAccessExpirationSeconds();

    JwtClaims parseAccessToken(String token);

    JwtClaims parseRefreshToken(String token);
}
