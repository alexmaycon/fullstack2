package br.com.jtech.tasklist.application.ports.output;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListPersistenceOutputGateway {

    TaskList save(UUID userId, TaskList taskList);

    Optional<TaskList> findByIdAndUserId(UUID id, UUID userId);

    List<TaskList> findAllByUserId(UUID userId);

    boolean existsByUserIdAndName(UUID userId, String name);

    boolean existsByUserIdAndNameAndIdNot(UUID userId, String name, UUID id);

    void deleteById(UUID id);
}
