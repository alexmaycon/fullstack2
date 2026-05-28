package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RenameAndFindTaskListUseCasesTest {

    @Mock
    private TaskListPersistenceOutputGateway persistence;

    @Test
    void shouldRenameSuccessfully() {
        RenameTaskListUseCase useCase = new RenameTaskListUseCase(persistence);
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        TaskList existing = TaskList.builder().id(listId).userId(userId).name("Old").build();
        when(persistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.of(existing));
        when(persistence.existsByUserIdAndNameAndIdNot(userId, "New", listId)).thenReturn(false);
        when(persistence.save(eq(userId), any(TaskList.class))).thenAnswer(inv -> inv.getArgument(1));

        TaskList result = useCase.rename(userId, listId, " New ");
        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void shouldThrowConflictOnRenameDuplicate() {
        RenameTaskListUseCase useCase = new RenameTaskListUseCase(persistence);
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        when(persistence.findByIdAndUserId(listId, userId))
                .thenReturn(Optional.of(TaskList.builder().id(listId).userId(userId).name("Old").build()));
        when(persistence.existsByUserIdAndNameAndIdNot(userId, "New", listId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.rename(userId, listId, "New"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void shouldThrowNotFoundOnRenameMissing() {
        RenameTaskListUseCase useCase = new RenameTaskListUseCase(persistence);
        UUID userId = UUID.randomUUID();
        UUID listId = UUID.randomUUID();
        when(persistence.findByIdAndUserId(listId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.rename(userId, listId, "X"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllByUser() {
        FindAllTaskListsByUserUseCase useCase = new FindAllTaskListsByUserUseCase(persistence);
        UUID userId = UUID.randomUUID();
        when(persistence.findAllByUserId(userId))
                .thenReturn(List.of(TaskList.builder().name("A").build()));
        List<TaskList> result = useCase.findAll(userId);
        assertThat(result).hasSize(1);
    }
}
