package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.UUID;

public interface RenameTaskListInputGateway {
    TaskList rename(UUID userId, UUID taskListId, String newName);
}
