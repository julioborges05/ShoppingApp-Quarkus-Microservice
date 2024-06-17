create sequence if not exists cart_sequence;

create table if not exists cart(
    id bigint not null default nextVal('cart_sequence'),
    total_price float,
    cart_status varchar(20) not null,
    user_id bigint not null,
    constraint cart_pkey primary key (id)
);


create sequence if not exists cart_v_product_sequence;

create table if not exists cart_v_product(
    id bigint not null default nextVal('cart_v_product_sequence'),
    cart_id bigint not null,
    product_id bigint not null,
    product_quantity integer not null default 1,
    constraint cart_v_product_pkey primary key (id),
    constraint cart_id_fkey foreign key (cart_id) references cart (id) on delete cascade
);
