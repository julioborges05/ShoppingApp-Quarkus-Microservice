create sequence if not exists product_sequence;

create table if not exists product(
    id bigint not null default nextVal('product_sequence'),
    name varchar(50) not null,
    price float not null,
    quantity integer not null default 0,
    constraint product_pkey primary key (id)
);

---

create sequence if not exists user_sequence;

create table if not exists users(
    id bigint not null default nextVal('user_sequence'),
    name varchar(50) not null,
    constraint users_pkey primary key (id)
);

---

create sequence if not exists cart_sequence;

create table if not exists cart(
    id bigint not null default nextVal('cart_sequence'),
    total_price float,
    cart_status varchar(20) not null,
    user_id bigint not null,
    constraint cart_pkey primary key (id),
    constraint user_id_fkey foreign key (user_id) references users (id) on delete cascade
);


create sequence if not exists cart_v_product_sequence;

create table if not exists cart_v_product(
    id bigint not null default nextVal('cart_v_product_sequence'),
    cart_id bigint not null,
    product_id bigint not null,
    product_quantity integer not null default 1,
    constraint cart_v_product_pkey primary key (id),
    constraint cart_id_fkey foreign key (cart_id) references cart (id) on delete cascade,
    constraint product_id_fkey foreign key (product_id) references product (id) on delete cascade
);