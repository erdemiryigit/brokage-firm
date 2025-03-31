-- Insert Assets
INSERT INTO asset (ticker, description) VALUES ('TRY', 'Turkish Lira');
INSERT INTO asset (ticker, description) VALUES ('TSLA', 'Tesla Inc.');
INSERT INTO asset (ticker, description) VALUES ('AAPL', 'Apple Inc.');
INSERT INTO asset (ticker, description) VALUES ('MSFT', 'Microsoft Corporation');
INSERT INTO asset (ticker, description) VALUES ('AMZN', 'Amazon.com Inc.');
INSERT INTO asset (ticker, description) VALUES ('GOOGL', 'Alphabet Inc.');

-- Insert Customers
INSERT INTO customers (id, name) VALUES (1, 'customer1');
INSERT INTO customers (id, name) VALUES (2, 'customer2');
INSERT INTO customers (id, name) VALUES (3, 'customer3');

-- Insert Roles
INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'EMPLOYEE');
INSERT INTO roles (id, name) VALUES (3, 'CUSTOMER');

-- Insert Users
INSERT INTO users (id, username, password) VALUES (1, 'admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG'); --password123
INSERT INTO users (id, username, password) VALUES (2, 'employee', '$2a$10$E/FLCSMu7A9v92FDWciR6.xRcsKu9tT0WJkIUfP9G4syOF1sIZ.lK'); --password456
INSERT INTO users (id, username, password) VALUES (3, 'customer1', '$2a$10$e87B94p61QJ5LPPwFudqt.YwEEJz/K5fNKTHdIvXwjnC.Y5JsyvMy'); --password789
INSERT INTO users (id, username, password) VALUES (4, 'customer2', '$2a$10$E/FLCSMu7A9v92FDWciR6.xRcsKu9tT0WJkIUfP9G4syOF1sIZ.lK'); --password456


-- Insert User-Role relationships
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO user_role (user_id, role_id) VALUES (2, 2);
INSERT INTO user_role (user_id, role_id) VALUES (3, 3);

-- Insert CustomerAssets (Funds in TRY)
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (1, 1, 'TRY', 10000.00, 10000.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (2, 2, 'TRY', 25000.00, 25000.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (3, 3, 'TRY', 5000.00, 5000.00);

-- Insert CustomerAssets (Stock holdings)
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (4, 1, 'TSLA', 10.00, 10.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (5, 1, 'AAPL', 20.00, 20.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (6, 2, 'MSFT', 15.00, 15.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (7, 2, 'AMZN', 5.00, 5.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size) VALUES (8, 3, 'GOOGL', 8.00, 8.00);

-- Insert Orders (mix of PENDING, MATCHED, and CANCELLED orders)
INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (1, 1, 'TSLA', 'BUY', 2.00, 180.50, 'PENDING', '2023-06-01 10:00:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (2, 1, 'AAPL', 'SELL', 5.00, 190.25, 'MATCHED', '2023-06-01 11:30:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (3, 2, 'MSFT', 'SELL', 3.00, 340.75, 'PENDING', '2023-06-02 09:15:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (4, 2, 'AMZN', 'BUY', 2.00, 128.30, 'CANCELLED', '2023-06-02 14:45:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (5, 3, 'GOOGL', 'SELL', 1.00, 122.80, 'MATCHED', '2023-06-03 10:30:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES (6, 3, 'TSLA', 'BUY', 3.00, 182.15, 'PENDING', '2023-06-03 15:20:00');

-- Reserve sequence values to match the manually inserted IDs
SELECT setval('customers_id_seq', 3, true);
SELECT setval('customer_asset_id_seq', 8, true);
SELECT setval('roles_id_seq', 3, true);
SELECT setval('users_id_seq', 3, true);
SELECT setval('orders_id_seq', 6, true);