create table admin_logs
(
    id          bigint primary key generated always as identity,
    user_id     bigint      references users (id) on delete set null,
    log_content text        not null check (char_length(log_content) between 1 and 200),
    log_type    text        not null check (log_type in ('CHAT_MESSAGE', 'DISCUSSION_MESSAGE', 'POST', 'COMMENT')),
    created_at  timestamptz not null
);
