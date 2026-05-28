package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.CountTasksByListOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTaskListUseCaseTest {

    @Mock
    private TaskListPersistenceOutputGateway persistence;

    @Mock
    private CountTasksByListOutputGateway counter;

    @InjectMocks
    private DeleteTaskListUseCase useCase;

    @Test
    void shouldDeleteWhenNoTasks() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        TaskList list = TaskList.builder().id(listId).userId(userId).name("X").build();
        when(persistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.of(list));
        when(counter.countByTaskListId(listId)).thenReturn(0L);

        useCase.delete(userId, listId);

        verify(persistence).deleteById(listId);
    }

    @Test
    void shouldThrowConflictWhenListHasTasks() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        when(persistence.findByIdAndUserId(listId, userId))
                .thenReturn(Optional.of(TaskList.builder().id(listId).userId(userId).name("X").build()));
        when(counter.countByTaskListId(listId)).thenReturn(3L);

        assertThatThrownBy(() -> useCase.delete(userId, listId))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldThrowNotFoundWhenListMissing() {
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        when(persistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.delete(userId, listId))
                .isInstanceOf(NotFoundException.class);
    }
}
