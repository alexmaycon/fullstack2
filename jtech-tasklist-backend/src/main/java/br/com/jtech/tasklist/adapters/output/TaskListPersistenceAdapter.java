package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.TaskListRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskListEntity;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.output.TaskListPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskListPersistenceAdapter implements TaskListPersistenceOutputGateway {

    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Override
    public TaskList save(UUID userId, TaskList taskList) {
        TaskListEntity entity;
        if (taskList.getId() != null) {
            entity = taskListRepository.findByIdAndUserId(taskList.getId(), userId)
                    .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada"));
            entity.setName(taskList.getName());
        } else {
            entity = TaskListEntity.builder()
                    .id(UUID.randomUUID())
                    .name(taskList.getName())
                    .user(userRepository.getReferenceById(userId))
                    .build();
        }
        TaskListEntity saved = taskListRepository.save(entity);
        return TaskList.of(saved);
    }

    @Override
    public Optional<TaskList> findByIdAndUserId(UUID id, UUID userId) {
        return taskListRepository.findByIdAndUserId(id, userId).map(TaskList::of);
    }

    @Override
    public List<TaskList> findAllByUserId(UUID userId) {
        return taskListRepository.findAllByUserIdOrderByNameAsc(userId).stream()
                .map(TaskList::of)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndName(UUID userId, String name) {
        return taskListRepository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    @Override
    public boolean existsByUserIdAndNameAndIdNot(UUID userId, String name, UUID id) {
        return taskListRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, id);
    }

    @Override
    public void deleteById(UUID id) {
        taskListRepository.deleteById(id);
    }
}
