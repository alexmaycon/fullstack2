package br.com.jtech.tasklist.adapters.output.repositories;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    Optional<TaskEntity> findByIdAndUserId(UUID id, UUID userId);

    Page<TaskEntity> findAllByUserId(UUID userId, Pageable pageable);

    Page<TaskEntity> findAllByUserIdAndTaskListId(UUID userId, UUID taskListId, Pageable pageable);

    boolean existsByTaskListIdAndTitleIgnoreCase(UUID taskListId, String title);

    boolean existsByTaskListIdAndTitleIgnoreCaseAndIdNot(UUID taskListId, String title, UUID id);

    long countByTaskListId(UUID taskListId);
}
