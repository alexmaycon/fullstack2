package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.PasswordEncoderOutputGateway;
import br.com.jtech.tasklist.application.ports.output.SaveUserOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock private SaveUserOutputGateway saveUser;
    @Mock private PasswordEncoderOutputGateway encoder;

    @InjectMocks
    private RegisterUserUseCase useCase;

    @Test
    void shouldRegisterUserAndEncodePassword() {
        User input = User.builder().name("Alex").email("a@a.com").password("raw").build();
        when(saveUser.existsByEmail("a@a.com")).thenReturn(false);
        when(encoder.encode("raw")).thenReturn("hash");
        when(saveUser.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        User result = useCase.register(input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getPassword()).isEqualTo("hash");
    }

    @Test
    void shouldThrowConflictWhenEmailExists() {
        User input = User.builder().name("Alex").email("a@a.com").password("raw").build();
        when(saveUser.existsByEmail("a@a.com")).thenReturn(true);
        assertThatThrownBy(() -> useCase.register(input))
                .isInstanceOf(ConflictException.class);
    }
}
