package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.User;

import java.util.Optional;

public interface FindUserByEmailInputGateway {
    Optional<User> findByEmail(String email);
}
