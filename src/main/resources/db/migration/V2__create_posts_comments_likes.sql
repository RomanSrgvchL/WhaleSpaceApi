create table posts
(
    id         bigint primary key generated always as identity,
    user_id    bigint references users (id) on delete cascade,
    content    text        not null check (char_length(content) between 1 and 2000),
    created_at timestamptz not null
);

create table post_image_file_names
(
    post_id         bigint not null references posts (id) on delete cascade,
    image_file_name text   not null
);

create table post_likes
(
    id         bigint primary key generated always as identity,
    user_id    bigint      not null references users (id) on delete cascade,
    post_id    bigint      not null references posts (id) on delete cascade,
    created_at timestamptz not null,
    unique (user_id, post_id)
);

create table comments
(
    id         bigint primary key generated always as identity,
    user_id    bigint references users (id) on delete cascade,
    post_id    bigint      not null references posts (id) on delete cascade,
    content    text        not null check (char_length(content) between 1 and 1000),
    created_at timestamptz not null
);

create table comment_likes
(
    id         bigint primary key generated always as identity,
    user_id    bigint      not null references users (id) on delete cascade,
    comment_id bigint      not null references comments (id) on delete cascade,
    created_at timestamptz not null,
    unique (user_id, comment_id)
);