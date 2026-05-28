package br.com.jtech.tasklist.application.core.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthTokens {
    private final UUID userId;
    private final String email;
    private final String accessToken;
    private final String refreshToken;
    private final long expiresInSeconds;
}
