package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.CreateTaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class CreateTaskUseCase implements CreateTaskInputGateway {

    private final TaskPersistenceOutputGateway taskPersistence;
    private final TaskListPersistenceOutputGateway taskListPersistence;

    public CreateTaskUseCase(TaskPersistenceOutputGateway taskPersistence,
                             TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskPersistence = taskPersistence;
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public Task create(UUID userId, Task task) {
        TaskList list = taskListPersistence.findByIdAndUserId(task.getTaskListId(), userId)
                .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));

        String trimmedTitle = task.getTitle().trim();
        if (taskPersistence.existsByTaskListIdAndTitle(list.getId(), trimmedTitle)) {
            throw new ConflictException("Já existe uma tarefa com este título nesta lista");
        }
        task.setId(null);
        task.setTitle(trimmedTitle);
        task.setUserId(userId);
        task.setTaskListId(list.getId());
        if (task.getCompleted() == null) {
            task.setCompleted(Boolean.FALSE);
        }
        return taskPersistence.create(userId, list.getId(), task);
    }
}
