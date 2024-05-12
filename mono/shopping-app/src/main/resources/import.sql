insert into product(id, name, price, quantity) values
(nextVal('product_sequence'), 'coca-cola', 7.5, 5),
(nextVal('product_sequence'), 'frango desfiado', 29.9, 10),
(nextVal('product_sequence'), 'picanha', 105.49, 2);

---

insert into users(id, name) values
(nextVal('user_sequence'), 'Júlio Borges'),
(nextVal('user_sequence'), 'Fulano da Silva'),
(nextVal('user_sequence'), 'Quarkus user');

---

insert into cart(id, total_price, cart_status, user_id) values
(nextVal('cart_sequence'), null, 'PENDING', 1),
(nextVal('cart_sequence'), null, 'PENDING', 2),
(nextVal('cart_sequence'), null, 'PENDING', 3);

insert into cart_v_product(id, cart_id, product_id, product_quantity) values
(nextVal('cart_v_product_sequence'), 1, 1, 2),
(nextVal('cart_v_product_sequence'), 1, 2, 3),
(nextVal('cart_v_product_sequence'), 1, 3, 3),
(nextVal('cart_v_product_sequence'), 2, 1, 1),
(nextVal('cart_v_product_sequence'), 2, 2, 5),
(nextVal('cart_v_product_sequence'), 3, 1, 1),
(nextVal('cart_v_product_sequence'), 3, 2, 2);