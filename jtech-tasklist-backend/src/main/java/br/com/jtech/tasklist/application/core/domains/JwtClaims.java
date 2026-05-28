package br.com.jtech.tasklist.application.core.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class JwtClaims {
    private final UUID userId;
    private final String email;
}
