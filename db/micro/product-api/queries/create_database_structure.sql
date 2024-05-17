create sequence if not exists product_sequence;

create table if not exists product(
    id bigint not null default nextVal('product_sequence'),
    name varchar(50) not null,
    price float not null,
    quantity integer not null default 0,
    constraint product_pkey primary key (id)
);