CREATE SCHEMA IF NOT EXISTS user_management;

CREATE TABLE IF NOT EXISTS user_management.t_users
(
    id         SERIAL PRIMARY KEY,
    c_username VARCHAR(50) NOT NULL CHECK ( length(trim(c_username)) > 0 ) UNIQUE,
    c_password VARCHAR
);

CREATE TABLE IF NOT EXISTS user_management.t_authority
(
    id          SERIAL PRIMARY KEY,
    c_authority VARCHAR(50) NOT NULL CHECK ( length(trim(c_authority)) > 0 ) UNIQUE
);

CREATE TABLE IF NOT EXISTS user_management.t_user_authority
(
    id         SERIAL PRIMARY KEY,
    id_user    INTEGER REFERENCES user_management.t_users (id),
    id_authority INTEGER REFERENCES user_management.t_authority (id),
    CONSTRAINT uq_user_authority UNIQUE (id_user, id_authority)
);