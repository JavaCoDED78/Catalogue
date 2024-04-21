CREATE SCHEMA IF NOT EXISTS catalogue;

CREATE TABLE IF NOT EXISTS catalogue.t_product
(
    id        SERIAL PRIMARY KEY,
    c_title   VARCHAR(50) NOT NULL CHECK (length(trim(c_title)) >= 3),
    c_details VARCHAR(1000)
);
