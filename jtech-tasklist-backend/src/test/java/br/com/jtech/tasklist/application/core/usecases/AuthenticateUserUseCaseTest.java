package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.FindUserOutputGateway;
import br.com.jtech.tasklist.application.ports.output.JwtTokenOutputGateway;
import br.com.jtech.tasklist.application.ports.output.PasswordEncoderOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock private FindUserOutputGateway findUser;
    @Mock private PasswordEncoderOutputGateway encoder;
    @Mock private JwtTokenOutputGateway jwt;

    @InjectMocks
    private AuthenticateUserUseCase useCase;

    @Test
    void shouldReturnTokensOnSuccess() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("a@a.com").password("hash").name("X").build();
        when(findUser.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(encoder.matches("raw", "hash")).thenReturn(true);
        when(jwt.generateAccessToken(any(), any())).thenReturn("access");
        when(jwt.generateRefreshToken(any(), any())).thenReturn("refresh");
        when(jwt.getAccessExpirationSeconds()).thenReturn(3600L);

        AuthTokens tokens = useCase.authenticate("a@a.com", "raw");

        assertThat(tokens.getAccessToken()).isEqualTo("access");
        assertThat(tokens.getRefreshToken()).isEqualTo("refresh");
        assertThat(tokens.getExpiresInSeconds()).isEqualTo(3600L);
    }

    @Test
    void shouldThrowUnauthorizedWhenUserMissing() {
        when(findUser.findByEmail("x")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.authenticate("x", "y"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowUnauthorizedWhenPasswordMismatch() {
        User user = User.builder().id(UUID.randomUUID()).email("a@a.com").password("hash").build();
        when(findUser.findByEmail("a@a.com")).thenReturn(Optional.of(user));
        when(encoder.matches("bad", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.authenticate("a@a.com", "bad"))
                .isInstanceOf(UnauthorizedException.class);
    }
}
