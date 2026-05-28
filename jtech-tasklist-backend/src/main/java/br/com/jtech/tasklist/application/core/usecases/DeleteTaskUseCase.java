package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class DeleteTaskUseCase implements DeleteTaskInputGateway {

    private final TaskPersistenceOutputGateway taskPersistence;

    public DeleteTaskUseCase(TaskPersistenceOutputGateway taskPersistence) {
        this.taskPersistence = taskPersistence;
    }

    @Override
    public void delete(UUID userId, UUID taskId) {
        Task existing = taskPersistence.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));
        taskPersistence.deleteById(existing.getId());
    }
}
