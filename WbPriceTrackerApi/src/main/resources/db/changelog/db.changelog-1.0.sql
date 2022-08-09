--liquibase formatted sql

--changeset steshabolk:1
CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     VARCHAR(128)        NOT NULL,
    username VARCHAR(128) UNIQUE NOT NULL,
    email    VARCHAR(128) UNIQUE NOT NULL,
    password VARCHAR             NOT NULL,
    role     VARCHAR(128)        NOT NULL
);
--rollback DROP TABLE users;


--changeset steshabolk:2
CREATE TABLE IF NOT EXISTS items
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code  BIGINT UNIQUE NOT NULL,
    brand VARCHAR(128)  NOT NULL,
    name  VARCHAR(128)  NOT NULL
);
--rollback DROP TABLE items;


--changeset steshabolk:3
CREATE TABLE IF NOT EXISTS users_items
(
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    item_id BIGINT REFERENCES items (id) ON DELETE CASCADE
);
--rollback DROP TABLE users_items;


--changeset steshabolk:4
CREATE TABLE IF NOT EXISTS prices
(
    id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
    price   INT       NOT NULL,
    date    TIMESTAMP NOT NULL
);
--rollback DROP TABLE prices;