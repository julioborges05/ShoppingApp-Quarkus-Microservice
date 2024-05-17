create sequence if not exists user_sequence;

create table if not exists users(
    id bigint not null default nextVal('user_sequence'),
    name varchar(50) not null,
    constraint users_pkey primary key (id)
);