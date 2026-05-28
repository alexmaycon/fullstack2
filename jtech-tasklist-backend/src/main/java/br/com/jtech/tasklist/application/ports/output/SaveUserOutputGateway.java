package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.User;

public interface SaveUserOutputGateway {
    User save(User user);

    boolean existsByEmail(String email);
}
