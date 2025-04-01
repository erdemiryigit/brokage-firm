-- Insert Assets (ticker is still a string ID, no change needed)
INSERT INTO asset (ticker, description)
VALUES ('TRY', 'Turkish Lira');
INSERT INTO asset (ticker, description)
VALUES ('TSLA', 'Tesla Inc.');
INSERT INTO asset (ticker, description)
VALUES ('AAPL', 'Apple Inc.');
INSERT INTO asset (ticker, description)
VALUES ('MSFT', 'Microsoft Corporation');
INSERT INTO asset (ticker, description)
VALUES ('AMZN', 'Amazon.com Inc.');
INSERT INTO asset (ticker, description)
VALUES ('GOOGL', 'Alphabet Inc.');

-- Insert Customers with UUIDs
INSERT INTO customers (id, name)
VALUES ('a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6', 'customer1');
INSERT INTO customers (id, name)
VALUES ('b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6', 'customer2');
INSERT INTO customers (id, name)
VALUES ('c1c2c3c4-d1d2-e1e2-f1f2-a1a2a3a4a5a6', 'customer3');

-- Insert Roles with UUIDs
INSERT INTO roles (id, name)
VALUES ('d1d2d3d4-e1e2-f1f2-a1a2-b1b2b3b4b5b6', 'ADMIN');
INSERT INTO roles (id, name)
VALUES ('e1e2e3e4-f1f2-a1a2-b1b2-c1c2c3c4c5c6', 'EMPLOYEE');
INSERT INTO roles (id, name)
VALUES ('f1f2f3f4-a1a2-b1b2-c1c2-d1d2d3d4d5d6', 'CUSTOMER');

-- Insert Users with UUIDs (password = username)
INSERT INTO users (id, username, password)
VALUES ('a2a3a4a5-b2b3-c2c3-d2d3-e2e3e4e5e6e7',
        'admin',
        '$2a$10$KWy3ApIGp4V1bI/p11iOJuQ7mY3p0QRokr8YGMSqyAZP6m17.ZsdC');
INSERT INTO users (id, username, password)
VALUES ('b2b3b4b5-c2c3-d2d3-e2e3-f2f3f4f5f6f7',
        'employee',
        '$2a$10$UVH7SVYmF1B0NTe5hTBtmuJmqJ4M0MBY87DMigpFdGtOhhx/ZVRqK');
INSERT INTO users (id, username, password)
VALUES ('c2c3c4c5-d2d3-e2e3-f2f3-a2a3a4a5a6a7',
        'customer1',
        '$2a$10$gMa/0T8zyX0lFVVNtMvU9eyFpNKvA6IxEi4hxn0LS9pWVbPlNrV7i');
INSERT INTO users (id, username, password)
VALUES ('d2d3d4d5-e2e3-f2f3-a2a3-b2b3b4b5b6b7',
        'customer2',
        '$2a$10$ZGqDw/shj2Wl5Wk1pDsHeeKVyjyFTt9UpEWQVZcvfnLXpY5TvqZLG');

-- Insert User-Role relationships
INSERT INTO user_role (user_id, role_id)
VALUES ('a2a3a4a5-b2b3-c2c3-d2d3-e2e3e4e5e6e7',
        'd1d2d3d4-e1e2-f1f2-a1a2-b1b2b3b4b5b6');
INSERT INTO user_role (user_id, role_id)
VALUES ('b2b3b4b5-c2c3-d2d3-e2e3-f2f3f4f5f6f7',
        'e1e2e3e4-f1f2-a1a2-b1b2-c1c2c3c4c5c6');
INSERT INTO user_role (user_id, role_id)
VALUES ('c2c3c4c5-d2d3-e2e3-f2f3-a2a3a4a5a6a7',
        'f1f2f3f4-a1a2-b1b2-c1c2-d1d2d3d4d5d6');
INSERT INTO user_role (user_id, role_id)
VALUES ('d2d3d4d5-e2e3-f2f3-a2a3-b2b3b4b5b6b7',
        'f1f2f3f4-a1a2-b1b2-c1c2-d1d2d3d4d5d6');

-- Insert CustomerAssets with UUIDs
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('a3a4a5a6-b3b4-c3c4-d3d4-e3e4e5e6e7e8',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'TRY', 10000.00, 10000.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('b3b4b5b6-c3c4-d3d4-e3e4-f3f4f5f6f7f8',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'TRY', 25000.00, 25000.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('c3c4c5c6-d3d4-e3e4-f3f4-a3a4a5a6a7a8',
        'c1c2c3c4-d1d2-e1e2-f1f2-a1a2a3a4a5a6',
        'TRY', 5000.00, 5000.00);

-- Insert Stock holdings
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('d3d4d5d6-e3e4-f3f4-a3a4-b3b4b5b6b7b8',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'TSLA', 10.00, 10.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('e3e4e5e6-f3f4-a3a4-b3b4-c3c4c5c6c7c8',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'AAPL', 20.00, 20.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('f3f4f5f6-a3a4-b3b4-c3c4-d3d4d5d6d7d8',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'MSFT', 15.00, 15.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('a5a6a7a8-b5b6-c5c6-d5d6-e5e6e7e8e9f0',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'AMZN', 5.00, 5.00);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size)
VALUES ('b5b6b7b8-c5c6-d5d6-e5e6-f5f6f7f8f9e0',
        'c1c2c3c4-d1d2-e1e2-f1f2-a1a2a3a4a5a6',
        'GOOGL', 8.00, 8.00);

-- Insert Orders with UUIDs
INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('a4a5a6a7-b4b5-c4c5-d4d5-e4e5e6e7e8e9',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'TSLA', 'BUY', 2.00, 180.50, 'PENDING', '2023-06-01 10:00:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('b4b5b6b7-c4c5-d4d5-e4e5-f4f5f6f7f8f9',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'AAPL', 'SELL', 5.00, 190.25, 'MATCHED', '2023-06-01 11:30:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('c4c5c6c7-d4d5-e4e5-f4f5-a4a5a6a7a8a9',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'MSFT', 'SELL', 3.00, 340.75, 'PENDING', '2023-06-02 09:15:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('d4d5d6d7-e4e5-f4f5-a4a5-b4b5b6b7b8b9',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'AMZN', 'BUY', 2.00, 128.30, 'CANCELLED', '2023-06-02 14:45:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('e4e5e6e7-f4f5-a4a5-b4b5-c4c5c6c7c8c9',
        'c1c2c3c4-d1d2-e1e2-f1f2-a1a2a3a4a5a6',
        'GOOGL', 'SELL', 1.00, 122.80, 'MATCHED', '2023-06-03 10:30:00');

INSERT INTO orders (id, customer_id, asset_ticker, order_side, size, price, status, create_date)
VALUES ('f4f5f6f7-a4a5-b4b5-c4c5-d4d5d6d7d8d9',
        'c1c2c3c4-d1d2-e1e2-f1f2-a1a2a3a4a5a6',
        'TSLA', 'BUY', 3.00, 182.15, 'PENDING', '2023-06-03 15:20:00');