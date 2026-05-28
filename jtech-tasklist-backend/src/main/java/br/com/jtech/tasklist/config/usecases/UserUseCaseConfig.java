package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.adapters.output.FindUserAdapter;
import br.com.jtech.tasklist.adapters.output.JwtTokenAdapter;
import br.com.jtech.tasklist.adapters.output.SaveUserAdapter;
import br.com.jtech.tasklist.application.core.usecases.AuthenticateUserUseCase;
import br.com.jtech.tasklist.application.core.usecases.RefreshTokenUseCase;
import br.com.jtech.tasklist.application.core.usecases.RegisterUserUseCase;
import br.com.jtech.tasklist.application.ports.input.AuthenticateUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.RefreshTokenInputGateway;
import br.com.jtech.tasklist.application.ports.input.RegisterUserInputGateway;
import br.com.jtech.tasklist.application.ports.output.PasswordEncoderOutputGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public RegisterUserInputGateway registerUserUseCase(SaveUserAdapter saveUserAdapter,
                                                        PasswordEncoderOutputGateway passwordEncoderOutputGateway) {
        return new RegisterUserUseCase(saveUserAdapter, passwordEncoderOutputGateway);
    }

    @Bean
    public AuthenticateUserInputGateway authenticateUserUseCase(FindUserAdapter findUserAdapter,
                                                                PasswordEncoderOutputGateway passwordEncoderOutputGateway,
                                                                JwtTokenAdapter jwtTokenAdapter) {
        return new AuthenticateUserUseCase(findUserAdapter, passwordEncoderOutputGateway, jwtTokenAdapter);
    }

    @Bean
    public RefreshTokenInputGateway refreshTokenUseCase(JwtTokenAdapter jwtTokenAdapter,
                                                        FindUserAdapter findUserAdapter) {
        return new RefreshTokenUseCase(jwtTokenAdapter, findUserAdapter);
    }
}
