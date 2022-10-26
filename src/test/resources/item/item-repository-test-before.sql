INSERT INTO users (user_id, name, email)
VALUES (1, 'Adam', 'adam@paradise.comm');

INSERT INTO users (user_id, name, email)
VALUES (2, 'Eva', 'eva@paradise.com');

INSERT INTO requests (request_id, description, requester_id, created)
VALUES (4, 'great garden', 2, '2022-10-24 12:30:00');

INSERT INTO items (item_id, name, description, is_available, owner_id, request_id)
VALUES (3, 'Paradise', 'great garden without people', true, 1, 4);