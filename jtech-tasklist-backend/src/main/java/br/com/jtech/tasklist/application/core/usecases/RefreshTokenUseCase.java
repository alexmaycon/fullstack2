package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.JwtClaims;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.RefreshTokenInputGateway;
import br.com.jtech.tasklist.application.ports.output.FindUserOutputGateway;
import br.com.jtech.tasklist.application.ports.output.JwtTokenOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;

public class RefreshTokenUseCase implements RefreshTokenInputGateway {

    private final JwtTokenOutputGateway jwtTokenOutputGateway;
    private final FindUserOutputGateway findUserOutputGateway;

    public RefreshTokenUseCase(JwtTokenOutputGateway jwtTokenOutputGateway,
                               FindUserOutputGateway findUserOutputGateway) {
        this.jwtTokenOutputGateway = jwtTokenOutputGateway;
        this.findUserOutputGateway = findUserOutputGateway;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        JwtClaims claims;
        try {
            claims = jwtTokenOutputGateway.parseRefreshToken(refreshToken);
        } catch (RuntimeException ex) {
            throw new UnauthorizedException("Refresh token inválido ou expirado");
        }

        User user = findUserOutputGateway.findById(claims.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));

        String newAccess = jwtTokenOutputGateway.generateAccessToken(user.getId(), user.getEmail());
        String newRefresh = jwtTokenOutputGateway.generateRefreshToken(user.getId(), user.getEmail());

        return AuthTokens.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .expiresInSeconds(jwtTokenOutputGateway.getAccessExpirationSeconds())
                .build();
    }
}
