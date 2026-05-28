package br.com.jtech.tasklist.adapters.input.controllers;

import br.com.jtech.tasklist.adapters.input.protocols.TaskListRequest;
import br.com.jtech.tasklist.adapters.input.protocols.TaskListResponse;
import br.com.jtech.tasklist.application.core.domains.TaskList;
import br.com.jtech.tasklist.application.ports.input.CreateTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.input.DeleteTaskListInputGateway;
import br.com.jtech.tasklist.application.ports.input.FindAllTaskListsByUserInputGateway;
import br.com.jtech.tasklist.application.ports.input.RenameTaskListInputGateway;
import br.com.jtech.tasklist.config.infra.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
@Tag(name = "TaskLists", description = "Gerenciamento de listas de tarefas")
public class TaskListController {

    private final CreateTaskListInputGateway createTaskListInputGateway;
    private final FindAllTaskListsByUserInputGateway findAllTaskListsByUserInputGateway;
    private final RenameTaskListInputGateway renameTaskListInputGateway;
    private final DeleteTaskListInputGateway deleteTaskListInputGateway;

    @Operation(summary = "Criar nova lista de tarefas")
    @PostMapping
    public ResponseEntity<TaskListResponse> create(@Valid @RequestBody TaskListRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        TaskList created = createTaskListInputGateway.create(userId, request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskListResponse.of(created));
    }

    @Operation(summary = "Listar todas as listas do usuário autenticado")
    @GetMapping
    public ResponseEntity<List<TaskListResponse>> findAll() {
        UUID userId = SecurityUtils.getCurrentUserId();
        List<TaskListResponse> response = findAllTaskListsByUserInputGateway.findAll(userId).stream()
                .map(TaskListResponse::of)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Renomear lista de tarefas")
    @PutMapping("/{id}")
    public ResponseEntity<TaskListResponse> rename(@PathVariable UUID id,
                                                   @Valid @RequestBody TaskListRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        TaskList updated = renameTaskListInputGateway.rename(userId, id, request.getName());
        return ResponseEntity.ok(TaskListResponse.of(updated));
    }

    @Operation(summary = "Excluir lista de tarefas (não permitido se tiver tarefas)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        deleteTaskListInputGateway.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
