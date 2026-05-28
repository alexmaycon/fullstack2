package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskQueryAndDeleteUseCasesTest {

    @Mock
    private TaskPersistenceOutputGateway taskPersistence;
    @Mock
    private TaskListPersistenceOutputGateway taskListPersistence;

    @Test
    void shouldFindTaskByIdSuccessfully() {
        FindTaskByIdUseCase useCase = new FindTaskByIdUseCase(taskPersistence);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task t = Task.builder().id(taskId).build();
        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(t));
        assertThat(useCase.findById(userId, taskId).getId()).isEqualTo(taskId);
    }

    @Test
    void shouldThrowNotFoundOnFindMissing() {
        FindTaskByIdUseCase useCase = new FindTaskByIdUseCase(taskPersistence);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.findById(userId, taskId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllByUserWithoutFilter() {
        FindAllTasksByUserUseCase useCase = new FindAllTasksByUserUseCase(taskPersistence, taskListPersistence);
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> page = new PageImpl<>(List.of(Task.builder().build()));
        when(taskPersistence.findAllByUserId(userId, pageable)).thenReturn(page);
        assertThat(useCase.findAll(userId, null, pageable).getContent()).hasSize(1);
    }

    @Test
    void shouldFindAllByUserFilteredByList() {
        FindAllTasksByUserUseCase useCase = new FindAllTasksByUserUseCase(taskPersistence, taskListPersistence);
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        when(taskListPersistence.findByIdAndUserId(listId, userId))
                .thenReturn(Optional.of(TaskList.builder().id(listId).userId(userId).name("L").build()));
        when(taskPersistence.findAllByUserIdAndTaskListId(userId, listId, pageable))
                .thenReturn(new PageImpl<>(List.of(Task.builder().build())));
        assertThat(useCase.findAll(userId, listId, pageable).getContent()).hasSize(1);
    }

    @Test
    void shouldDeleteTaskSuccessfully() {
        DeleteTaskUseCase useCase = new DeleteTaskUseCase(taskPersistence);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskPersistence.findByIdAndUserId(taskId, userId))
                .thenReturn(Optional.of(Task.builder().id(taskId).build()));
        useCase.delete(userId, taskId);
        verify(taskPersistence).deleteById(taskId);
    }

    @Test
    void shouldThrowNotFoundOnDeleteMissing() {
        DeleteTaskUseCase useCase = new DeleteTaskUseCase(taskPersistence);
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.delete(userId, taskId))
                .isInstanceOf(NotFoundException.class);
    }
}
