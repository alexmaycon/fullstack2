package br.com.jtech.tasklist.application.ports.input;

import java.util.UUID;

public interface DeleteTaskListInputGateway {
    void delete(UUID userId, UUID taskListId);
}
