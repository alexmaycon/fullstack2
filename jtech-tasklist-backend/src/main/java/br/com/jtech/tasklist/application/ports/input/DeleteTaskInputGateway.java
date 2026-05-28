package br.com.jtech.tasklist.application.ports.input;

import java.util.UUID;

public interface DeleteTaskInputGateway {
    void delete(UUID userId, UUID taskId);
}
