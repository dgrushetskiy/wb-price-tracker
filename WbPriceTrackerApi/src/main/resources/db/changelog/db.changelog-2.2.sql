--liquibase formatted sql

--changeset steshabolk:1
INSERT INTO prices (item_id, price, date)
VALUES ((SELECT id FROM items WHERE code = 15061503), 1100, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 32956137), 550, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 70455609), 2990, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37955601), 1900, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 19364027), 1550, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 62880505), 5280, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 39404188), 2541, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37110732), 2789, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 8118885), 725, (TIMESTAMP '2022-07-28 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 14566666), 2241, (TIMESTAMP '2022-07-28 12:0:0.0'));
--rollback TRUNCATE TABLE prices;

--changeset steshabolk:2
INSERT INTO prices (item_id, price, date)
VALUES ((SELECT id FROM items WHERE code = 15061503), 1202, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 32956137), 568, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 70455609), 2963, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37955601), 1777, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 19364027), 1521, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 62880505), 5322, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 39404188), 2492, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37110732), 2860, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 8118885), 769, (TIMESTAMP '2022-07-29 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 14566666), 2247, (TIMESTAMP '2022-07-29 12:0:0.0'));
--rollback TRUNCATE TABLE prices;

--changeset steshabolk:3
INSERT INTO prices (item_id, price, date)
VALUES ((SELECT id FROM items WHERE code = 15061503), 1233, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 32956137), 555, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 70455609), 2970, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37955601), 1833, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 19364027), 1434, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 62880505), 5052, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 39404188), 2468, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 37110732), 2742, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 8118885), 701, (TIMESTAMP '2022-07-30 12:0:0.0')),
       ((SELECT id FROM items WHERE code = 14566666), 2213, (TIMESTAMP '2022-07-30 12:0:0.0'));
--rollback TRUNCATE TABLE prices;