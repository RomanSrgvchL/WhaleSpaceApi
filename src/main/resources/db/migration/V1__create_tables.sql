create table users
(
    id               bigint primary key generated always as identity,
    username         text      not null unique check (char_length(username) between 1 and 20),
    password         text      not null check (char_length(password) between 1 and 100),
    created_at       timestamp not null,
    role             text      not null,
    avatar_file_name text,
    birth_date       date,
    gender           text,
    bio              text check (char_length(bio) between 1 and 120)
);

create table chats
(
    id         bigint primary key generated always as identity,
    user1_id   bigint    not null references users (id) on delete cascade,
    user2_id   bigint    not null references users (id) on delete cascade,
    created_at timestamp not null,
    unique (user1_id, user2_id)
);

create table chat_messages
(
    id         bigint primary key generated always as identity,
    chat_id    bigint    not null references chats (id) on delete cascade,
    content    text      not null check (char_length(content) between 1 and 200),
    sender_id  bigint    not null references users (id) on delete cascade,
    created_at timestamp not null
);

create table discussions
(
    id         bigint primary key generated always as identity,
    title      text      not null check (char_length(title) between 5 and 100),
    creator_id bigint    not null references users (id) on delete cascade,
    created_at timestamp not null
);

create table discussion_messages
(
    id            bigint primary key generated always as identity,
    discussion_id bigint    not null references discussions (id) on delete cascade,
    content       text      not null check (char_length(content) between 1 and 200),
    sender_id     bigint    not null references users (id) on delete cascade,
    created_at    timestamp not null
);