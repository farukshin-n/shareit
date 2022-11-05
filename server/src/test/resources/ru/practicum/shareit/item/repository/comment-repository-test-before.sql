INSERT INTO users (user_id, name, email)
VALUES (1, 'Adam', 'adam@paradise.comm');

INSERT INTO users (user_id, name, email)
VALUES (2, 'Eva', 'eva@paradise.com');

INSERT INTO items (item_id, name, description, available, owner_id, request_id)
VALUES (3, 'Paradise', 'great garden without people', true, 1, null);

INSERT INTO comments (comment_id, text, item_id, author_id, created)
VALUES (4, 'great garden!', 3, 2, '2022-12-12 12:12:00');