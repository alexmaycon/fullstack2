--liquibase formatted sql

--changeset ALEX:4
CREATE INDEX idx_task_lists_user_id ON task_lists (user_id);
CREATE INDEX idx_tasks_user_id ON tasks (user_id);
CREATE INDEX idx_tasks_task_list_id ON tasks (task_list_id);
--rollback DROP INDEX idx_tasks_task_list_id;
--rollback DROP INDEX idx_tasks_user_id;
--rollback DROP INDEX idx_task_lists_user_id;
