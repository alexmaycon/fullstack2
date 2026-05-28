--liquibase formatted sql

--changeset ALEX:3
CREATE TABLE tasks (
    id UUID NOT NULL,
    title VARCHAR(180) NOT NULL,
    description VARCHAR(2000),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    due_date TIMESTAMP,
    user_id UUID NOT NULL,
    task_list_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (id),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_tasks_task_list FOREIGN KEY (task_list_id) REFERENCES task_lists (id),
    CONSTRAINT uk_tasks_list_title UNIQUE (task_list_id, title)
);
--rollback DROP TABLE tasks;
