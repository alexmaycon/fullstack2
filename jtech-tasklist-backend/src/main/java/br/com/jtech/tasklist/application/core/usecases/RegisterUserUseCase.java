package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.RegisterUserInputGateway;
import br.com.jtech.tasklist.application.ports.output.PasswordEncoderOutputGateway;
import br.com.jtech.tasklist.application.ports.output.SaveUserOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;

public class RegisterUserUseCase implements RegisterUserInputGateway {

    private final SaveUserOutputGateway saveUserOutputGateway;
    private final PasswordEncoderOutputGateway passwordEncoderOutputGateway;

    public RegisterUserUseCase(SaveUserOutputGateway saveUserOutputGateway,
                               PasswordEncoderOutputGateway passwordEncoderOutputGateway) {
        this.saveUserOutputGateway = saveUserOutputGateway;
        this.passwordEncoderOutputGateway = passwordEncoderOutputGateway;
    }

    @Override
    public User register(User user) {
        if (saveUserOutputGateway.existsByEmail(user.getEmail())) {
            throw new ConflictException("E-mail já cadastrado");
        }
        user.setId(null);
        user.setPassword(passwordEncoderOutputGateway.encode(user.getPassword()));
        return saveUserOutputGateway.save(user);
    }
}
