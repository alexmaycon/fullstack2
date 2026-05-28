package br.com.jtech.tasklist.application.ports.output;

import java.util.UUID;

public interface CountTasksByListOutputGateway {
    long countByTaskListId(UUID taskListId);
}
