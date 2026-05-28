package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    private UUID id;
    private String title;
    private String description;
    private Boolean completed;
    private OffsetDateTime dueDate;
    private UUID userId;
    private UUID taskListId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static Task of(TaskEntity entity) {
        if (entity == null) {
            return null;
        }
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .completed(entity.getCompleted())
                .dueDate(entity.getDueDate())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .taskListId(entity.getTaskList() != null ? entity.getTaskList().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
