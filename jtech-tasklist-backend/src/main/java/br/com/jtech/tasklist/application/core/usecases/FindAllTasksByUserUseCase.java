package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.FindAllTasksByUserInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public class FindAllTasksByUserUseCase implements FindAllTasksByUserInputGateway {

    private final TaskPersistenceOutputGateway taskPersistence;
    private final TaskListPersistenceOutputGateway taskListPersistence;

    public FindAllTasksByUserUseCase(TaskPersistenceOutputGateway taskPersistence,
                                     TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskPersistence = taskPersistence;
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public Page<Task> findAll(UUID userId, UUID taskListId, Pageable pageable) {
        if (taskListId != null) {
            taskListPersistence.findByIdAndUserId(taskListId, userId)
                    .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));
            return taskPersistence.findAllByUserIdAndTaskListId(userId, taskListId, pageable);
        }
        return taskPersistence.findAllByUserId(userId, pageable);
    }
}
