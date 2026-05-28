package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.FindTaskByIdInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class FindTaskByIdUseCase implements FindTaskByIdInputGateway {

    private final TaskPersistenceOutputGateway taskPersistence;

    public FindTaskByIdUseCase(TaskPersistenceOutputGateway taskPersistence) {
        this.taskPersistence = taskPersistence;
    }

    @Override
    public Task findById(UUID userId, UUID taskId) {
        return taskPersistence.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));
    }
}
