CREATE TYPE user_role AS ENUM (
    'USER',
    'VERIFIED'
);

CREATE TABLE IF NOT EXISTS users (
    id       SERIAL,
    email    TEXT NOT NULL,
    name     TEXT        NOT NULL,
    password TEXT        NOT NULL,
    role     user_role   NOT NULL,
    enabled  BOOLEAN     NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at TIMESTAMP WITH TIME ZONE,
    last_signed_in_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);