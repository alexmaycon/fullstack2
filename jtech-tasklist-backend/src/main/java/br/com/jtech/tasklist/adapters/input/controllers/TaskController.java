package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskResponse;
import br.com.jtech.tasklist.application.core.domains.Task;
import br.com.jtech.tasklist.application.ports.input.CreateTaskInputGateway;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindAllTasksByUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindTaskByIdInputGateway;
import br.com.jtech.tasklist.application.ports.input.UpdateTaskInputGateway;
import br.com.jtech.tasklist.config.infra.security.SecurityUtils;
import br.com.jtech.tasklist.config.infra.validation.ICreateValidationGroup;
import br.com.jtech.tasklist.config.infra.validation.IUpdateValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Gerenciamento de tarefas")
public class TaskController {

    private final CreateTaskInputGateway createTaskInputGateway;
    private final FindTaskByIdInputGateway findTaskByIdInputGateway;
    private final FindAllTasksByUserInputGateway findAllTasksByUserInputGateway;
    private final UpdateTaskInputGateway updateTaskInputGateway;
    private final DeleteTaskInputGateway deleteTaskInputGateway;

    @Operation(summary = "Criar nova tarefa")
    @PostMapping
    public ResponseEntity<TaskResponse> create(@Validated(ICreateValidationGroup.class)
                                               @RequestBody TaskRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Task created = createTaskInputGateway.create(userId, request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.of(created));
    }

    @Operation(summary = "Buscar tarefa por id")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Task task = findTaskByIdInputGateway.findById(userId, id);
        return ResponseEntity.ok(TaskResponse.of(task));
    }

    @Operation(summary = "Listar tarefas do usuário autenticado, opcionalmente filtradas por lista")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findAll(@RequestParam(required = false) UUID taskListId,
                                                      @PageableDefault(size = 20) Pageable pageable) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Page<TaskResponse> page = findAllTasksByUserInputGateway.findAll(userId, taskListId, pageable)
                .map(TaskResponse::of);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Atualizar tarefa existente")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id,
                                               @Validated(IUpdateValidationGroup.class)
                                               @RequestBody TaskRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Task updated = updateTaskInputGateway.update(userId, id, request.toDomain());
        return ResponseEntity.ok(TaskResponse.of(updated));
    }

    @Operation(summary = "Excluir tarefa")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        deleteTaskInputGateway.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
