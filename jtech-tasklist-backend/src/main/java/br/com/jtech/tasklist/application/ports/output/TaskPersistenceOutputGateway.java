package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TaskPersistenceOutputGateway {

    Task create(UUID userId, UUID taskListId, Task task);

    Task update(Task task);

    Optional<Task> findByIdAndUserId(UUID id, UUID userId);

    Page<Task> findAllByUserId(UUID userId, Pageable pageable);

    Page<Task> findAllByUserIdAndTaskListId(UUID userId, UUID taskListId, Pageable pageable);

    boolean existsByTaskListIdAndTitle(UUID taskListId, String title);

    boolean existsByTaskListIdAndTitleAndIdNot(UUID taskListId, String title, UUID id);

    void deleteById(UUID id);
}
