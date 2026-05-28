package br.com.jtech.tasklist.config.usecases;

import br.com.jtech.tasklist.application.core.usecases.CreateTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.DeleteTaskUseCase;
import br.com.jtech.tasklist.application.core.usecases.FindAllTasksByUserUseCase;
import br.com.jtech.tasklist.application.core.usecases.FindTaskByIdUseCase;
import br.com.jtech.tasklist.application.core.usecases.UpdateTaskUseCase;
import br.com.jtech.tasklist.application.ports.input.CreateTaskInputGateway;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindAllTasksByUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindTaskByIdInputGateway;
import br.com.jtech.tasklist.application.ports.input.UpdateTaskInputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskUseCaseConfig {

    @Bean
    public CreateTaskInputGateway createTaskUseCase(TaskPersistenceOutputGateway taskPersistence,
                                                    TaskListPersistenceOutputGateway taskListPersistence) {
        return new CreateTaskUseCase(taskPersistence, taskListPersistence);
    }

    @Bean
    public FindTaskByIdInputGateway findTaskByIdUseCase(TaskPersistenceOutputGateway taskPersistence) {
        return new FindTaskByIdUseCase(taskPersistence);
    }

    @Bean
    public FindAllTasksByUserInputGateway findAllTasksByUserUseCase(TaskPersistenceOutputGateway taskPersistence,
                                                                    TaskListPersistenceOutputGateway taskListPersistence) {
        return new FindAllTasksByUserUseCase(taskPersistence, taskListPersistence);
    }

    @Bean
    public UpdateTaskInputGateway updateTaskUseCase(TaskPersistenceOutputGateway taskPersistence,
                                                    TaskListPersistenceOutputGateway taskListPersistence) {
        return new UpdateTaskUseCase(taskPersistence, taskListPersistence);
    }

    @Bean
    public DeleteTaskInputGateway deleteTaskUseCase(TaskPersistenceOutputGateway taskPersistence) {
        return new DeleteTaskUseCase(taskPersistence);
    }
}
