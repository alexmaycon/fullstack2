package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;
import br.com.jtech.tasklist.application.core.domains.JwtClaims;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.FindUserOutputGateway;
import br.com.jtech.tasklist.application.ports.output.JwtTokenOutputGateway;
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
class RefreshTokenUseCaseTest {

    @Mock private JwtTokenOutputGateway jwt;
    @Mock private FindUserOutputGateway findUser;

    @InjectMocks
    private RefreshTokenUseCase useCase;

    @Test
    void shouldIssueNewTokens() {
        UUID userId = UUID.randomUUID();
        when(jwt.parseRefreshToken("ref")).thenReturn(JwtClaims.builder().userId(userId).email("a@a.com").build());
        when(findUser.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).email("a@a.com").build()));
        when(jwt.generateAccessToken(any(), any())).thenReturn("newAccess");
        when(jwt.generateRefreshToken(any(), any())).thenReturn("newRefresh");
        when(jwt.getAccessExpirationSeconds()).thenReturn(3600L);

        AuthTokens tokens = useCase.refresh("ref");
        assertThat(tokens.getAccessToken()).isEqualTo("newAccess");
        assertThat(tokens.getRefreshToken()).isEqualTo("newRefresh");
    }

    @Test
    void shouldThrowUnauthorizedOnInvalidToken() {
        when(jwt.parseRefreshToken("bad")).thenThrow(new RuntimeException("boom"));
        assertThatThrownBy(() -> useCase.refresh("bad"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void shouldThrowUnauthorizedWhenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(jwt.parseRefreshToken("ref")).thenReturn(JwtClaims.builder().userId(userId).email("x").build());
        when(findUser.findById(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.refresh("ref"))
                .isInstanceOf(UnauthorizedException.class);
    }
}
