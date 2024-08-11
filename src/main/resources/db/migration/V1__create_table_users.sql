CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE user_role AS ENUM (
    'ROLE_USER',
    'ROLE_VERIFIED'
);

CREATE TABLE IF NOT EXISTS users (
    id       SERIAL,
    email    TEXT NOT NULL,
    name     TEXT        NOT NULL,
    password TEXT        NOT NULL,
    profile_image_key TEXT NOT NULL,
    role     user_role   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    last_signed_in_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);