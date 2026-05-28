package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.AuthResponse;
import br.com.jtech.tasklist.adapters.input.protocols.LoginRequest;
import br.com.jtech.tasklist.adapters.input.protocols.RefreshTokenRequest;
import br.com.jtech.tasklist.adapters.input.protocols.RegisterUserRequest;
import br.com.jtech.tasklist.adapters.input.protocols.UserResponse;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.input.AuthenticateUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.RefreshTokenInputGateway;
import br.com.jtech.tasklist.application.ports.input.RegisterUserInputGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e registro de usuários")
@SecurityRequirements
public class AuthController {

    private final RegisterUserInputGateway registerUserInputGateway;
    private final AuthenticateUserInputGateway authenticateUserInputGateway;
    private final RefreshTokenInputGateway refreshTokenInputGateway;

    @Operation(summary = "Registrar novo usuário")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User created = registerUserInputGateway.register(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(created));
    }

    @Operation(summary = "Autenticar usuário e obter tokens JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var tokens = authenticateUserInputGateway.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(AuthResponse.of(tokens));
    }

    @Operation(summary = "Renovar tokens a partir de um refresh token válido")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var tokens = refreshTokenInputGateway.refresh(request.getRefreshToken());
        return ResponseEntity.ok(AuthResponse.of(tokens));
    }
}
