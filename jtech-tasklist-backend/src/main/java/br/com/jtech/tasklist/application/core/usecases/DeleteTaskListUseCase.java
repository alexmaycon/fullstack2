package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.output.CountTasksByListOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;

import java.util.UUID;

public class DeleteTaskListUseCase implements DeleteTaskListInputGateway {

    private final TaskListPersistenceOutputGateway taskListPersistence;
    private final CountTasksByListOutputGateway countTasksByListOutputGateway;

    public DeleteTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence,
                                 CountTasksByListOutputGateway countTasksByListOutputGateway) {
        this.taskListPersistence = taskListPersistence;
        this.countTasksByListOutputGateway = countTasksByListOutputGateway;
    }

    @Override
    public void delete(UUID userId, UUID taskListId) {
        TaskList existing = taskListPersistence.findByIdAndUserId(taskListId, userId)
                .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));

        long taskCount = countTasksByListOutputGateway.countByTaskListId(existing.getId());
        if (taskCount > 0) {
            throw new ConflictException("Não é possível excluir uma lista que possui tarefas");
        }
        taskListPersistence.deleteById(existing.getId());
    }
}
