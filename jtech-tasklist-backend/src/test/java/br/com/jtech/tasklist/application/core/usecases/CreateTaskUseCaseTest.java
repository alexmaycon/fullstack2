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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskPersistenceOutputGateway taskPersistence;
    @Mock
    private TaskListPersistenceOutputGateway taskListPersistence;

    @InjectMocks
    private CreateTaskUseCase useCase;

    @Test
    void shouldCreateTaskSuccessfully() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Task input = Task.builder().title("  Estudar  ").taskListId(listId).build();
        TaskList list = TaskList.builder().id(listId).userId(userId).name("L").build();

        when(taskListPersistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.of(list));
        when(taskPersistence.existsByTaskListIdAndTitle(listId, "Estudar")).thenReturn(false);
        when(taskPersistence.create(eq(userId), eq(listId), any(Task.class)))
                .thenAnswer(inv -> {
                    Task t = inv.getArgument(2);
                    t.setId(UUID.randomUUID());
                    return t;
                });

        Task result = useCase.create(userId, input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Estudar");
        assertThat(result.getCompleted()).isFalse();
    }

    @Test
    void shouldThrowNotFoundWhenListNotOwned() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Task input = Task.builder().title("X").taskListId(listId).build();
        when(taskListPersistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.create(userId, input))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowConflictWhenDuplicateTitle() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        Task input = Task.builder().title("X").taskListId(listId).build();
        when(taskListPersistence.findByIdAndUserId(listId, userId))
                .thenReturn(Optional.of(TaskList.builder().id(listId).userId(userId).name("L").build()));
        when(taskPersistence.existsByTaskListIdAndTitle(listId, "X")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(userId, input))
                .isInstanceOf(ConflictException.class);
    }
}
