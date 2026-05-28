package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.Task;

import java.util.UUID;

public interface FindTaskByIdInputGateway {
    Task findById(UUID userId, UUID taskId);
}
