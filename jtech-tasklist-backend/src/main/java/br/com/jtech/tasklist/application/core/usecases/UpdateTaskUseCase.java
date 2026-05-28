package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.UpdateTaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class UpdateTaskUseCase implements UpdateTaskInputGateway {

    private final TaskPersistenceOutputGateway taskPersistence;
    private final TaskListPersistenceOutputGateway taskListPersistence;

    public UpdateTaskUseCase(TaskPersistenceOutputGateway taskPersistence,
                             TaskListPersistenceOutputGateway taskListPersistence) {
        this.taskPersistence = taskPersistence;
        this.taskListPersistence = taskListPersistence;
    }

    @Override
    public Task update(UUID userId, UUID taskId, Task task) {
        Task existing = taskPersistence.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));

        UUID targetListId = task.getTaskListId() != null ? task.getTaskListId() : existing.getTaskListId();
        if (!targetListId.equals(existing.getTaskListId())) {
            TaskList list = taskListPersistence.findByIdAndUserId(targetListId, userId)
                    .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));
            existing.setTaskListId(list.getId());
        }

        String trimmedTitle = task.getTitle().trim();
        if (taskPersistence.existsByTaskListIdAndTitleAndIdNot(existing.getTaskListId(), trimmedTitle, existing.getId())) {
            throw new ConflictException("Já existe uma tarefa com este título nesta lista");
        }

        existing.setTitle(trimmedTitle);
        existing.setDescription(task.getDescription());
        existing.setDueDate(task.getDueDate());
        if (task.getCompleted() != null) {
            existing.setCompleted(task.getCompleted());
        }
        return taskPersistence.update(existing);
    }
}
