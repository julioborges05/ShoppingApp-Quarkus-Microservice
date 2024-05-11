insert into product(id, name, price, quantity) values
(nextVal('product_sequence'), 'coca-cola', 7.5, 5),
(nextVal('product_sequence'), 'frango desfiado', 29.9, 10),
(nextVal('product_sequence'), 'picanha', 105.49, 2);

---

insert into users(id, name) values
(nextVal('user_sequence'), 'Júlio Borges'),
(nextVal('user_sequence'), 'Fulano da Silva'),
(nextVal('user_sequence'), 'Quarkus user');