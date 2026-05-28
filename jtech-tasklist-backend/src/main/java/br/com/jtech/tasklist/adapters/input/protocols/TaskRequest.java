package br.com.jtech.tasklist.adapters.input.protocols;

import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.config.infra.validation.ICreateValidationGroup;
import br.com.jtech.tasklist.config.infra.validation.IUpdateValidationGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TaskRequest implements Serializable {

    @Schema(description = "Identificador da lista (obrigatório na criação)")
    @NotNull(groups = ICreateValidationGroup.class, message = "ID da lista é obrigatório")
    private UUID taskListId;

    @Schema(description = "Título da tarefa")
    @NotBlank(groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class}, message = "Título é obrigatório")
    @Size(max = 180, groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
            message = "Título deve ter no máximo 180 caracteres")
    private String title;

    @Schema(description = "Descrição da tarefa")
    @Size(max = 2000, groups = {ICreateValidationGroup.class, IUpdateValidationGroup.class},
            message = "Descrição deve ter no máximo 2000 caracteres")
    private String description;

    @Schema(description = "Indica se a tarefa está concluída", example = "false")
    private Boolean completed;

    @Schema(description = "Data limite da tarefa")
    private OffsetDateTime dueDate;

    public Task toDomain() {
        return Task.builder()
                .taskListId(taskListId)
                .title(title)
                .description(description)
                .completed(completed)
                .dueDate(dueDate)
                .build();
    }
}
