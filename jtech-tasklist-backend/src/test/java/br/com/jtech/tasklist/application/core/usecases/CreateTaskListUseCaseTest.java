package br.com.jtech.tasklist.application.core.usecases;

import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskListUseCaseTest {

    @Mock
    private TaskListPersistenceOutputGateway persistence;

    @InjectMocks
    private CreateTaskListUseCase useCase;

    @Test
    void shouldCreateTaskListSuccessfully() {
        UUID userId = UUID.randomUUID();
        when(persistence.existsByUserIdAndName(userId, "Trabalho")).thenReturn(false);
        when(persistence.save(eq(userId), any(TaskList.class))).thenAnswer(inv -> {
            TaskList t = inv.getArgument(1);
            t.setId(UUID.randomUUID());
            return t;
        });

        TaskList result = useCase.create(userId, "  Trabalho  ");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Trabalho");
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void shouldThrowConflictWhenDuplicateName() {
        UUID userId = UUID.randomUUID();
        when(persistence.existsByUserIdAndName(eq(userId), eq("Trabalho"))).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(userId, "Trabalho"))
                .isInstanceOf(ConflictException.class);
    }
}
