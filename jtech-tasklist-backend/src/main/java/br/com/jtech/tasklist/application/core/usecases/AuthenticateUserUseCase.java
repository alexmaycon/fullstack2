package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.AuthenticateUserInputGateway;
import br.com.jtech.tasklist.application.ports.output.FindUserOutputGateway;
import br.com.jtech.tasklist.application.ports.output.JwtTokenOutputGateway;
import br.com.jtech.tasklist.application.ports.output.PasswordEncoderOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;

public class AuthenticateUserUseCase implements AuthenticateUserInputGateway {

    private final FindUserOutputGateway findUserOutputGateway;
    private final PasswordEncoderOutputGateway passwordEncoderOutputGateway;
    private final JwtTokenOutputGateway jwtTokenOutputGateway;

    public AuthenticateUserUseCase(FindUserOutputGateway findUserOutputGateway,
                                   PasswordEncoderOutputGateway passwordEncoderOutputGateway,
                                   JwtTokenOutputGateway jwtTokenOutputGateway) {
        this.findUserOutputGateway = findUserOutputGateway;
        this.passwordEncoderOutputGateway = passwordEncoderOutputGateway;
        this.jwtTokenOutputGateway = jwtTokenOutputGateway;
    }

    @Override
    public AuthTokens authenticate(String email, String rawPassword) {
        User user = findUserOutputGateway.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!passwordEncoderOutputGateway.matches(rawPassword, user.getPassword())) {
            throw new UnauthorizedException("Credenciais inválidas");
        }

        String access = jwtTokenOutputGateway.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtTokenOutputGateway.generateRefreshToken(user.getId(), user.getEmail());

        return AuthTokens.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(access)
                .refreshToken(refresh)
                .expiresInSeconds(jwtTokenOutputGateway.getAccessExpirationSeconds())
                .build();
    }
}
