package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskUseCaseTest {

    @Mock
    private TaskPersistenceOutputGateway taskPersistence;
    @Mock
    private TaskListPersistenceOutputGateway taskListPersistence;

    @InjectMocks
    private UpdateTaskUseCase useCase;

    @Test
    void shouldUpdateTaskSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Task existing = Task.builder().id(taskId).userId(userId).taskListId(listId)
                .title("Old").completed(false).build();
        Task input = Task.builder().title(" New ").completed(true).build();

        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existing));
        when(taskPersistence.existsByTaskListIdAndTitleAndIdNot(listId, "New", taskId)).thenReturn(false);
        when(taskPersistence.update(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = useCase.update(userId, taskId, input);
        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getCompleted()).isTrue();
    }

    @Test
    void shouldThrowNotFoundWhenMissing() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(userId, taskId, Task.builder().title("X").build()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldMoveTaskToAnotherList() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID oldList = UUID.randomUUID();
        UUID newList = UUID.randomUUID();
        Task existing = Task.builder().id(taskId).userId(userId).taskListId(oldList)
                .title("T").completed(false).build();
        Task input = Task.builder().title("T").taskListId(newList).build();

        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existing));
        when(taskListPersistence.findByIdAndUserId(newList, userId))
                .thenReturn(Optional.of(TaskList.builder().id(newList).userId(userId).name("L").build()));
        when(taskPersistence.existsByTaskListIdAndTitleAndIdNot(newList, "T", taskId)).thenReturn(false);
        when(taskPersistence.update(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = useCase.update(userId, taskId, input);
        assertThat(result.getTaskListId()).isEqualTo(newList);
    }

    @Test
    void shouldThrowConflictOnDuplicateTitle() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Task existing = Task.builder().id(taskId).userId(userId).taskListId(listId).title("T").build();
        when(taskPersistence.findByIdAndUserId(taskId, userId)).thenReturn(Optional.of(existing));
        when(taskPersistence.existsByTaskListIdAndTitleAndIdNot(listId, "T2", taskId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.update(userId, taskId, Task.builder().title("T2").build()))
                .isInstanceOf(ConflictException.class);
    }
}
