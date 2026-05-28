package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse implements Serializable {

    @Schema(description = "Identificador do usuário")
    private UUID userId;

    @Schema(description = "E-mail do usuário")
    private String email;

    @Schema(description = "Token de acesso JWT")
    private String accessToken;

    @Schema(description = "Token de atualização JWT")
    private String refreshToken;

    @Schema(description = "Tipo do token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Tempo de expiração do access token em segundos")
    private long expiresIn;

    public static AuthResponse of(AuthTokens tokens) {
        return AuthResponse.builder()
                .userId(tokens.getUserId())
                .email(tokens.getEmail())
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(tokens.getExpiresInSeconds())
                .build();
    }
}
