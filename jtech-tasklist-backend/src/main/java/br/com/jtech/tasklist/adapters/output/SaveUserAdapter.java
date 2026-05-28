package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.UserEntity;
import br.com.jtech.tasklist.application.core.domains.User;
import br.com.jtech.tasklist.application.ports.output.SaveUserOutputGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveUserAdapter implements SaveUserOutputGateway {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        UserEntity entity = user.toEntity();
        if (entity.getId() == null) {
            entity.setId(java.util.UUID.randomUUID());
        }
        UserEntity saved = userRepository.save(entity);
        return User.of(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
