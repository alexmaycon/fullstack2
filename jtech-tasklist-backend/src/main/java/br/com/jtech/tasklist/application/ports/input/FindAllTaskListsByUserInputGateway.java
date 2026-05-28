package br.com.jtech.tasklist.application.ports.input;

import br.com.jtech.tasklist.application.core.domains.TaskList;

import java.util.List;
import java.util.UUID;

public interface FindAllTaskListsByUserInputGateway {
    List<TaskList> findAll(UUID userId);
}
