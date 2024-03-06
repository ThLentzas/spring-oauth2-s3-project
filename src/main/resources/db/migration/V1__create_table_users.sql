CREATE TABLE IF NOT EXISTS users (
    id        SERIAL,
    email     VARCHAR(50) NOT NULL,
    name      TEXT NOT NULL,
    password  TEXT        NOT NULL,
    activated BOOLEAN     NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unique_users_email UNIQUE (email)
);