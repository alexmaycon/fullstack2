package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.CreateTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;

import java.util.UUID;

public class CreateTaskListUseCase implements CreateTaskListInputGateway {

    private final TaskListPersistenceOutputGateway taskListPersistence;

    public CreateTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public TaskList create(UUID userId, String name) {
        String trimmed = name.trim();
        if (taskListPersistence.existsByUserIdAndName(userId, trimmed)) {
            throw new ConflictException("Já existe uma lista com este nome");
        }
        TaskList taskList = TaskList.builder()
                .name(trimmed)
                .userId(userId)
                .build();
        return taskListPersistence.save(userId, taskList);
    }
}
