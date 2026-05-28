package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.FindAllTaskListsByUserInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;

import java.util.List;
import java.util.UUID;

public class FindAllTaskListsByUserUseCase implements FindAllTaskListsByUserInputGateway {

    private final TaskListPersistenceOutputGateway taskListPersistence;

    public FindAllTaskListsByUserUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public List<TaskList> findAll(UUID userId) {
        return taskListPersistence.findAllByUserId(userId);
    }
}
