package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.UUID;

public interface CreateTaskListInputGateway {
    TaskList create(UUID userId, String name);
}
