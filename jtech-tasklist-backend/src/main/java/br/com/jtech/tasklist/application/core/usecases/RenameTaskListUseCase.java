package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.RenameTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class RenameTaskListUseCase implements RenameTaskListInputGateway {

    private final TaskListPersistenceOutputGateway taskListPersistence;

    public RenameTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public TaskList rename(UUID userId, UUID taskListId, String newName) {
        TaskList existing = taskListPersistence.findByIdAndUserId(taskListId, userId)
                .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));

        String trimmed = newName.trim();
        if (taskListPersistence.existsByUserIdAndNameAndIdNot(userId, trimmed, taskListId)) {
            throw new ConflictException("Já existe uma lista com este nome");
        }
        existing.setName(trimmed);
        return taskListPersistence.save(userId, existing);
    }
}
