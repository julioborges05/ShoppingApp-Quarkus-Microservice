create sequence if not exists product_sequence;

create table product(
    id bigint default nextVal('product_sequence') not null,
    name varchar(50) not null,
    price float not null,
    constraint product_pkey primary key (id)
);