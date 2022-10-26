DELETE FROM bookings WHERE booking_id = 4;
DELETE FROM bookings WHERE booking_id = 6;
DELETE FROM bookings WHERE booking_id = 7;
ALTER TABLE bookings ALTER COLUMN booking_id RESTART WITH 1;

DELETE FROM items WHERE item_id = 3;
DELETE FROM items WHERE item_id = 5;
ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;

DELETE FROM users WHERE user_id = 1;
DELETE FROM users WHERE user_id = 2;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;