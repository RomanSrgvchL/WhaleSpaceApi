create table users
(
    id               int primary key generated always as identity,
    username         text      not null unique check (char_length(username) between 1 and 20),
    password         text      not null check (char_length(password) <= 100),
    created_at       timestamp not null,
    role             text      not null,
    avatar_file_name text
);

create table chats
(
    id         int primary key generated always as identity,
    user1_id   int       not null references users (id) on delete cascade,
    user2_id   int       not null references users (id) on delete cascade,
    created_at timestamp not null,
    unique (user1_id, user2_id)
);

create table messages
(
    id         int primary key generated always as identity,
    chat_id    int       not null references chats (id) on delete cascade,
    content    text      not null check (char_length(content) between 1 and 200),
    sender_id  int       not null references users (id) on delete cascade,
    created_at timestamp not null
);

create table discussions
(
    id         int primary key generated always as identity,
    title      text      not null check (char_length(title) between 5 and 100),
    creator_id int       not null references users (id) on delete cascade,
    created_at timestamp not null
);

create table replies
(
    id            int primary key generated always as identity,
    discussion_id int       not null references discussions (id) on delete cascade,
    content       text      not null check (char_length(content) between 1 and 200),
    sender_id     int       not null references users (id) on delete cascade,
    created_at    timestamp not null
);