-- Insert Assets
INSERT INTO asset (ticker, description)
VALUES ('TRY', 'Turkish Lira');
INSERT INTO asset (ticker, description)
VALUES ('EREGL', 'Eregli Demir ve Celik Fabrikalari T.A.S.');
INSERT INTO asset (ticker, description)
VALUES ('AAPL', 'Apple Inc.');
INSERT INTO asset (ticker, description)
VALUES ('MSFT', 'Microsoft Corporation');

-- Insert Roles with UUIDs
INSERT INTO roles (id, name)
VALUES ('d1d2d3d4-e1e2-f1f2-a1a2-b1b2b3b4b5b6', 'ADMIN');
INSERT INTO roles (id, name)
VALUES ('e1e2e3e4-f1f2-a1a2-b1b2-c1c2c3c4c5c6', 'EMPLOYEE');

-- Insert Users with proper discriminator values
-- Staff users (ADMIN)
INSERT INTO users (id, username, password, user_type)
VALUES ('a2a3a4a5-b2b3-c2c3-d2d3-e2e3e4e5e6e7',
        'admin',
        '$2a$10$KWy3ApIGp4V1bI/p11iOJuQ7mY3p0QRokr8YGMSqyAZP6m17.ZsdC',
        'STAFF');

-- Staff users (EMPLOYEE)
INSERT INTO users (id, username, password, user_type)
VALUES ('b2b3b4b5-c2c3-d2d3-e2e3-f2f3f4f5f6f7',
        'employee',
        '$2a$10$UVH7SVYmF1B0NTe5hTBtmuJmqJ4M0MBY87DMigpFdGtOhhx/ZVRqK',
        'STAFF');

-- Customer users
INSERT INTO users (id, username, password, user_type)
VALUES ('a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'customer1',
        '$2a$10$gMa/0T8zyX0lFVVNtMvU9eyFpNKvA6IxEi4hxn0LS9pWVbPlNrV7i',
        'CUSTOMER');

INSERT INTO users (id, username, password, user_type)
VALUES ('b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'customer2',
        '$2a$10$ZGqDw/shj2Wl5Wk1pDsHeeKVyjyFTt9UpEWQVZcvfnLXpY5TvqZLG',
        'CUSTOMER');

-- User-Role relationships for Staff
INSERT INTO user_role (user_id, role_id)
VALUES ('a2a3a4a5-b2b3-c2c3-d2d3-e2e3e4e5e6e7',
        'd1d2d3d4-e1e2-f1f2-a1a2-b1b2b3b4b5b6');
INSERT INTO user_role (user_id, role_id)
VALUES ('b2b3b4b5-c2c3-d2d3-e2e3-f2f3f4f5f6f7',
        'e1e2e3e4-f1f2-a1a2-b1b2-c1c2c3c4c5c6');

-- CustomerAssets
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size, version)
VALUES ('a3a4a5a6-b3b4-c3c4-d3d4-e3e4e5e6e7e8',
        'a1a2a3a4-b1b2-c1c2-d1d2-e1e2e3e4e5e6',
        'TRY', 10000.00, 10000.00, 0);
INSERT INTO customer_asset (id, customer_id, asset_ticker, size, usable_size, version)
VALUES ('b3b4b5b6-c3c4-d3d4-e3e4-f3f4f5f6f7f8',
        'b1b2b3b4-c1c2-d1d2-e1e2-f1f2f3f4f5f6',
        'TRY', 25000.00, 25000.00, 0);