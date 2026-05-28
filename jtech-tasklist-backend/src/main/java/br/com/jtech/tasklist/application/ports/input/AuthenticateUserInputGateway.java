package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.AuthTokens;

public interface AuthenticateUserInputGateway {
    AuthTokens authenticate(String email, String rawPassword);
}
