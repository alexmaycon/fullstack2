--liquibase formatted sql

--changeset ALEX:2
CREATE TABLE task_lists (
    id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_task_lists PRIMARY KEY (id),
    CONSTRAINT fk_task_lists_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_task_lists_user_name UNIQUE (user_id, name)
);
--rollback DROP TABLE task_lists;
