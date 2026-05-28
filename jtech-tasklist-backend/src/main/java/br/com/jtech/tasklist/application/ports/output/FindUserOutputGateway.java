package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.User;

import java.util.Optional;
import java.util.UUID;

public interface FindUserOutputGateway {
    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);
}
