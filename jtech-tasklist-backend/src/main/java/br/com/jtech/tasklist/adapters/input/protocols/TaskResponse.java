package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.application.core.domains.Task;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse implements Serializable {

    @Schema(description = "Identificador da tarefa")
    private UUID id;

    @Schema(description = "Título da tarefa")
    private String title;

    @Schema(description = "Descrição da tarefa")
    private String description;

    @Schema(description = "Indica se a tarefa está concluída")
    private Boolean completed;

    @Schema(description = "Data limite")
    private OffsetDateTime dueDate;

    @Schema(description = "ID da lista a que a tarefa pertence")
    private UUID taskListId;

    @Schema(description = "Data de criação")
    private OffsetDateTime createdAt;

    @Schema(description = "Data de atualização")
    private OffsetDateTime updatedAt;

    public static TaskResponse of(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.getCompleted())
                .dueDate(task.getDueDate())
                .taskListId(task.getTaskListId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
