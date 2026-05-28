package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FindAllTasksByUserInputGateway {
    Page<Task> findAll(UUID userId, UUID taskListId, Pageable pageable);
}
