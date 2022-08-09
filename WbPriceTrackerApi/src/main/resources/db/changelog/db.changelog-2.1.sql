--liquibase formatted sql

--changeset steshabolk:1
INSERT INTO users (name, username, email, password, role)
VALUES ('User1', 'User1', 'user1@gmail.com',
        '$2a$10$5.TwuHnograF/l7SpakAGucb7BKk7GMzQ7EtrbdtDAkTbuByaiNLC', 'ROLE_USER'),
       ( 'User2', 'User2', 'user2@gmail.com',
        '$2a$10$7xy7w4Huvb9dgww2/vYuZeUY8lbPRGGcmCn7MMJ3KhcKAHKr47rfa', 'ROLE_USER'),
       ( 'User3', 'User3', 'user3@gmail.com',
        '$2a$10$A7P5kqZAEks.EPHqxobjyuyU1Jdt.C.bEhvhkA6Seozo5uLm4bqe66', 'ROLE_USER'),
       ('User4', 'User4', 'user4@gmail.com',
        '$2a$10$zbBALCKZittfkSTgM1pQI.KNe6a24yvtboGrtyh2oJLdr2Oj/lSuG', 'ROLE_USER');
--rollback TRUNCATE TABLE users;


--changeset steshabolk:2
INSERT INTO items (code, brand, name)
VALUES (15061503, 'Ticle', 'Футболка'),
       (32956137, 'UZcotton', 'Лонгслив бег'),
       (70455609, 'the nana', 'Брюки'),
       (37955601, 'MOANNA', 'Юбка'),
       (19364027, 'TRENDY TRUTH', 'Рубашка удлиненная'),
       (62880505, 'CALZETTI', 'Сумка'),
       (39404188, 'Ticle', 'Худи'),
       (37110732, 'Paetki', 'Платье'),
       (8118885, 'Riccardo Donati', 'Ремень'),
       (14566666, 'Ravenclo', 'Худи');
--rollback TRUNCATE TABLE items;


--changeset steshabolk:3
INSERT INTO users_items (user_id, item_id)
VALUES ((SELECT id FROM users WHERE username = 'User1'), (SELECT id FROM items WHERE code = 15061503)),
       ((SELECT id FROM users WHERE username = 'User1'), (SELECT id FROM items WHERE code = 32956137)),
       ((SELECT id FROM users WHERE username = 'User1'), (SELECT id FROM items WHERE code = 70455609)),
       ((SELECT id FROM users WHERE username = 'User1'), (SELECT id FROM items WHERE code = 37955601)),

       ((SELECT id FROM users WHERE username = 'User2'), (SELECT id FROM items WHERE code = 15061503)),
       ((SELECT id FROM users WHERE username = 'User2'), (SELECT id FROM items WHERE code = 19364027)),
       ((SELECT id FROM users WHERE username = 'User2'), (SELECT id FROM items WHERE code = 62880505)),
       ((SELECT id FROM users WHERE username = 'User2'), (SELECT id FROM items WHERE code = 39404188)),

       ((SELECT id FROM users WHERE username = 'User3'), (SELECT id FROM items WHERE code = 39404188)),
       ((SELECT id FROM users WHERE username = 'User3'), (SELECT id FROM items WHERE code = 37110732)),
       ((SELECT id FROM users WHERE username = 'User3'), (SELECT id FROM items WHERE code = 8118885)),
       ((SELECT id FROM users WHERE username = 'User3'), (SELECT id FROM items WHERE code = 14566666)),

       ((SELECT id FROM users WHERE username = 'User4'), (SELECT id FROM items WHERE code = 15061503)),
       ((SELECT id FROM users WHERE username = 'User4'), (SELECT id FROM items WHERE code = 37955601)),
       ((SELECT id FROM users WHERE username = 'User4'), (SELECT id FROM items WHERE code = 39404188)),
       ((SELECT id FROM users WHERE username = 'User4'), (SELECT id FROM items WHERE code = 14566666));
--rollback TRUNCATE TABLE users_items;


--changeset steshabolk:4
INSERT INTO prices (item_id, price, date)
VALUES ((SELECT id FROM items WHERE code = 15061503), 1176, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 32956137), 556, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 70455609), 2990, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37955601), 1840, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 19364027), 1534, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 62880505), 5352, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 39404188), 2491, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37110732), 3000, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 8118885), 736, (TIMESTAMP '2022-07-27 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 14566666), 2271, (TIMESTAMP '2022-07-27 12:0:0.0'));
--rollback TRUNCATE TABLE prices;