INSERT INTO users (user_id, name, email)
VALUES (1, 'Adam', 'adam@paradise.comm');

INSERT INTO users (user_id, name, email)
VALUES (2, 'Eva', 'eva@paradise.com');

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (4, 'great garden without people', 2, '2022-10-15 15:15:00');

INSERT INTO items (item_id, name, description, available, owner_id, request_id)
VALUES (3, 'Paradise', 'great garden without people', true, 1, 4);

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (6, 'very tasty fruit', 1, '2022-10-20 12:12:00');

INSERT INTO items (item_id, name, description, available, owner_id, request_id)
VALUES (5, 'Apple', 'very tasty fruit', true, 2, 6);