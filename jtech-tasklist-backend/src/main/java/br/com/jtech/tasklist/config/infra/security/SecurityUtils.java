package br.com.jtech.tasklist.config.infra.security;

import br.com.jtech.tasklist.config.infra.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        return principal.getUserId();
    }

    @Getter
    @AllArgsConstructor
    public static class AuthenticatedUser {
        private final UUID userId;
        private final String email;
    }
}
