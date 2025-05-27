create table person
(
    id         int primary key generated always as identity,
    username   text      not null unique check (char_length(username) between 1 and 20),
    password   text      not null,
    created_at timestamp not null,
    role       text      not null
);

create table chat
(
    id         int primary key generated always as identity,
    user1_id   int       not null references person (id) on delete cascade,
    user2_id   int       not null references person (id) on delete cascade,
    created_at timestamp not null,
    unique (user1_id, user2_id)
);

create table message
(
    id         int primary key generated always as identity,
    chat_id    int       not null references chat (id) on delete cascade,
    content    text      not null check (char_length(content) between 1 and 200),
    sender_id  int       not null references person (id) on delete cascade ,
    created_at timestamp not null
);

create table discussion
(
    id         int primary key generated always as identity,
    title      text      not null check (char_length(title) between 5 and 100),
    creator_id int       not null references person (id) on delete cascade,
    created_at timestamp not null
);

create table reply
(
    id            int primary key generated always as identity,
    discussion_id int       not null references discussion (id) on delete cascade,
    content       text      not null check (char_length(content) between 1 and 200),
    sender_id     int       not null references person (id) on delete cascade ,
    created_at    timestamp not null
);