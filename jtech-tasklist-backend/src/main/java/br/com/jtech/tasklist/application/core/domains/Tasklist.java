package br.com.jtech.tasklist.application.core.domains;

import br.com.jtech.tasklist.adapters.output.repositories.entities.TaskListEntity;
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
public class TaskList {

    private UUID id;
    private String name;
    private UUID userId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static TaskList of(TaskListEntity entity) {
        if (entity == null) {
            return null;
        }
        return TaskList.builder()
                .id(entity.getId())
                .name(entity.getName())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
