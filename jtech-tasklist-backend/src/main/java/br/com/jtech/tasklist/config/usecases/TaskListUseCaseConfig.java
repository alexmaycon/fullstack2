package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.CreateTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskListUseCase;
import br.com.jtech.tasklist.application.core.usecases.FindAllTaskListsByUserUseCase;
import br.com.jtech.tasklist.application.core.usecases.RenameTaskListUseCase;
import br.com.jtech.tasklist.application.ports.input.CreateTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindAllTaskListsByUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.RenameTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.output.CountTasksByListOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskListUseCaseConfig {

    @Bean
    public CreateTaskListInputGateway createTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        return new CreateTaskListUseCase(taskListPersistence);
    }

    @Bean
    public FindAllTaskListsByUserInputGateway findAllTaskListsByUserUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        return new FindAllTaskListsByUserUseCase(taskListPersistence);
    }

    @Bean
    public RenameTaskListInputGateway renameTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence) {
        return new RenameTaskListUseCase(taskListPersistence);
    }

    @Bean
    public DeleteTaskListInputGateway deleteTaskListUseCase(TaskListPersistenceOutputGateway taskListPersistence,
                                                            CountTasksByListOutputGateway countTasksByListOutputGateway) {
        return new DeleteTaskListUseCase(taskListPersistence, countTasksByListOutputGateway);
    }
}
