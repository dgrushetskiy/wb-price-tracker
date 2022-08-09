--liquibase formatted sql

--changeset steshabolk:1
INSERT INTO users (name, username, email, password, role)
VALUES ('AdminProfile', 'AdminProfile', 'admin@gmail.com',
        '$2a$10$9dOQPqlCW/VzKAuD2QgdROPK9yVrYyC45iup2l4koJdBDqYEERV2e', 'ROLE_ADMIN');
--rollback DELETE FROM users WHERE username = 'Admin';