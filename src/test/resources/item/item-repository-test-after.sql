DELETE FROM items WHERE item_id = 3;
ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;

DELETE FROM requests WHERE request_id = 4;
ALTER TABLE requests ALTER COLUMN request_id RESTART WITH 1;

DELETE FROM users WHERE user_id = 1;
DELETE FROM users WHERE user_id = 2;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;