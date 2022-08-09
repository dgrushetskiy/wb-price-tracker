-- from db.change-log2.0
-- Admin(1, "AdminProfile", "AdminProfile", "admin@gmail.com","adminPassword", 'ROLE_ADMIN');
-- from db.change-log2.1
-- User1(2, "User1", "User1", "user1@gmail.com","password1", 'ROLE_USER');
-- User2(3, "User2", "User2", "user2@gmail.com","password2", 'ROLE_USER');
-- User3(4, "User3", "User3", "user3@gmail.com","password3", 'ROLE_USER');
-- User4(5, "User4", "User4", "user4@gmail.com","password4", 'ROLE_USER');

-- TestUser1(6, "TestUser1", "TestUser1", "testUser1@gmail.com","password", 'ROLE_USER');
-- TestUser2(7, "TestUser2", "TestUser2", "testUser2@gmail.com","password", 'ROLE_USER');
-- TestUser3(8, "TestUser3", "TestUser3", "testUser3@gmail.com","password", 'ROLE_USER');
-- TestUser4(9, "TestUser4", "TestUser4", "testUser4@gmail.com","password", 'ROLE_USER');

INSERT INTO users (id, name, username, email, password, role) OVERRIDING SYSTEM VALUE
VALUES (6, 'TestUser1', 'TestUser1', 'testUser1@gmail.com',
        '$2a$10$cFptlXrB/OlKFsZdw7sQ.eXo8EY9dJ.CfEo3L82pWFB3Dzd71Abk6', 'ROLE_USER'),
       (7, 'TestUser2', 'TestUser2', 'testUser2@gmail.com',
        '$2a$10$N.Wa4RWFFtCEM8gIafI9Nexmgk/1f.EML4PzXRiPweZUST1LhrXDW', 'ROLE_USER'),
       (8, 'TestUser3', 'TestUser3', 'testUser3@gmail.com',
        '$2a$10$Jr/bn/6cN1gpWwEyNoazw.k3eEr0Je9A1gQBjYJ0fFMxJU.M2RSne', 'ROLE_USER'),
       (9, 'TestUser4', 'TestUser4', 'testUser4@gmail.com',
        '$2a$10$H0/Pb.7cTkUNbaEVAs8iEOYSZSL1KTaU5JpAprgkEIClPveLjOmgW', 'ROLE_USER');
SELECT SETVAL('users_id_seq', (SELECT MAX(id) FROM users));


INSERT INTO items (id, code, brand, name) OVERRIDING SYSTEM VALUE
VALUES (11, 15061497, 'Ticle', 'Футболка'),
       (12, 13458162, 'UZcotton', 'Лонгслив бег'),
       (13, 70456258, 'the nana', 'Брюки'),
       (14, 62602786, 'MOANNA', 'Юбка'),
       (15, 23384909, 'TRENDY TRUTH', 'Рубашка удлиненная'),
       (16, 62880510, 'CALZETTI', 'Сумка'),
       (17, 14648460, 'Ticle', 'Худи'),
       (18, 67161988, 'Paetki', 'Платье'),
       (19, 15650035, 'Riccardo Donati', 'Ремень'),
       (20, 12052239, 'Ravenclo', 'Худи');
SELECT SETVAL('items_id_seq', (SELECT MAX(id) FROM items));


INSERT INTO users_items (user_id, item_id) OVERRIDING SYSTEM VALUE
VALUES ((SELECT id FROM users WHERE username = 'TestUser1'), (SELECT id FROM items WHERE code = 15061497)),
       ((SELECT id FROM users WHERE username = 'TestUser1'), (SELECT id FROM items WHERE code = 13458162)),
       ((SELECT id FROM users WHERE username = 'TestUser1'), (SELECT id FROM items WHERE code = 70456258)),
       ((SELECT id FROM users WHERE username = 'TestUser1'), (SELECT id FROM items WHERE code = 62602786)),

       ((SELECT id FROM users WHERE username = 'TestUser2'), (SELECT id FROM items WHERE code = 15061497)),
       ((SELECT id FROM users WHERE username = 'TestUser2'), (SELECT id FROM items WHERE code = 23384909)),
       ((SELECT id FROM users WHERE username = 'TestUser2'), (SELECT id FROM items WHERE code = 62880510)),
       ((SELECT id FROM users WHERE username = 'TestUser2'), (SELECT id FROM items WHERE code = 14648460)),

       ((SELECT id FROM users WHERE username = 'TestUser3'), (SELECT id FROM items WHERE code = 14648460)),
       ((SELECT id FROM users WHERE username = 'TestUser3'), (SELECT id FROM items WHERE code = 67161988)),
       ((SELECT id FROM users WHERE username = 'TestUser3'), (SELECT id FROM items WHERE code = 15650035)),
       ((SELECT id FROM users WHERE username = 'TestUser3'), (SELECT id FROM items WHERE code = 12052239)),

       ((SELECT id FROM users WHERE username = 'TestUser4'), (SELECT id FROM items WHERE code = 15061497)),
       ((SELECT id FROM users WHERE username = 'TestUser4'), (SELECT id FROM items WHERE code = 62602786)),
       ((SELECT id FROM users WHERE username = 'TestUser4'), (SELECT id FROM items WHERE code = 14648460)),
       ((SELECT id FROM users WHERE username = 'TestUser4'), (SELECT id FROM items WHERE code = 12052239));


INSERT INTO prices (id, item_id, price, date) OVERRIDING SYSTEM VALUE
VALUES (41, (SELECT id FROM items WHERE code = 15061497), 1176, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (42, (SELECT id FROM items WHERE code = 13458162), 556, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (43, (SELECT id FROM items WHERE code = 70456258), 2990, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (44, (SELECT id FROM items WHERE code = 62602786), 1840, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (45, (SELECT id FROM items WHERE code = 23384909), 1534, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (46, (SELECT id FROM items WHERE code = 62880510), 5352, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (47, (SELECT id FROM items WHERE code = 14648460), 2491, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (48, (SELECT id FROM items WHERE code = 67161988), 3000, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (49, (SELECT id FROM items WHERE code = 15650035), 736, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (50, (SELECT id FROM items WHERE code = 12052239), 2271, (TIMESTAMP '2022-07-27 18:0:0.0')),
       (51, (SELECT id FROM items WHERE code = 15061497), 1100, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (52, (SELECT id FROM items WHERE code = 13458162), 500, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (53, (SELECT id FROM items WHERE code = 70456258), 2990, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (54, (SELECT id FROM items WHERE code = 62602786), 1850, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (55, (SELECT id FROM items WHERE code = 23384909), 1500, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (56, (SELECT id FROM items WHERE code = 62880510), 5300, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (57, (SELECT id FROM items WHERE code = 14648460), 2400, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (58, (SELECT id FROM items WHERE code = 67161988), 3100, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (59, (SELECT id FROM items WHERE code = 15650035), 750, (TIMESTAMP '2022-07-28 18:0:0.0')),
       (60, (SELECT id FROM items WHERE code = 12052239), 2270, (TIMESTAMP '2022-07-28 18:0:0.0'));
SELECT SETVAL('prices_id_seq', (SELECT MAX(id) FROM prices));


