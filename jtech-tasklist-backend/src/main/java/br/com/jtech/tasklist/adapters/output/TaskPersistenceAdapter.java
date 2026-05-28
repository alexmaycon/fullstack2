package br.com.jtech.tasklist.adapters.output;

import br.com.jtech.tasklist.adapters.output.repositories.TaskListRepository;
import br.com.jtech.tasklist.adapters.output.repositories.TaskRepository;
import br.com.jtech.tasklist.adapters.output.repositories.UserRepository;
import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.output.CountTasksByListOutputGateway;
import br.com.jtech.tasklist.application.ports.output.TaskPersistenceOutputGateway;
import br.com.jtech.tasklist.config.infra.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskPersistenceOutputGateway, CountTasksByListOutputGateway {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;

    @Override
    public Task create(UUID userId, UUID taskListId, Task task) {
        TaskEntity entity = TaskEntity.builder()
                .id(UUID.randomUUID())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted() != null ? task.getCompleted() : Boolean.FALSE)
                .dueDate(task.getDueDate())
                .user(userRepository.getReferenceById(userId))
                .taskList(taskListRepository.findByIdAndUserId(taskListId, userId)
                        .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada")))
                .build();
        return Task.of(taskRepository.save(entity));
    }

    @Override
    public Task update(Task task) {
        TaskEntity entity = taskRepository.findById(task.getId())
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setCompleted(task.getCompleted() != null ? task.getCompleted() : entity.getCompleted());
        entity.setDueDate(task.getDueDate());
        if (task.getTaskListId() != null && !task.getTaskListId().equals(entity.getTaskList().getId())) {
            entity.setTaskList(taskListRepository.findById(task.getTaskListId())
                    .orElseThrow(() -> new NotFoundException("Lista de tarefas não encontrada")));
        }
        return Task.of(taskRepository.save(entity));
    }

    @Override
    public Optional<Task> findByIdAndUserId(UUID id, UUID userId) {
        return taskRepository.findByIdAndUserId(id, userId).map(Task::of);
    }

    @Override
    public Page<Task> findAllByUserId(UUID userId, Pageable pageable) {
        return taskRepository.findAllByUserId(userId, pageable).map(Task::of);
    }

    @Override
    public Page<Task> findAllByUserIdAndTaskListId(UUID userId, UUID taskListId, Pageable pageable) {
        return taskRepository.findAllByUserIdAndTaskListId(userId, taskListId, pageable).map(Task::of);
    }

    @Override
    public boolean existsByTaskListIdAndTitle(UUID taskListId, String title) {
        return taskRepository.existsByTaskListIdAndTitleIgnoreCase(taskListId, title);
    }

    @Override
    public boolean existsByTaskListIdAndTitleAndIdNot(UUID taskListId, String title, UUID id) {
        return taskRepository.existsByTaskListIdAndTitleIgnoreCaseAndIdNot(taskListId, title, id);
    }

    @Override
    public void deleteById(UUID id) {
        taskRepository.deleteById(id);
    }

    @Override
    public long countByTaskListId(UUID taskListId) {
        return taskRepository.countByTaskListId(taskListId);
    }
}
