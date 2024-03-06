CREATE TYPE auth_provider AS ENUM (
    'GOOGLE',
    'GITHUB',
    'EMAIL'
);

CREATE TABLE IF NOT EXISTS providers (
    id SERIAL,
    auth_provider auth_provider NOT NULL,
    CONSTRAINT pk_providers PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users_providers (
    user_id INTEGER NOT NULL,
    provider_id INTEGER NOT NULL,
    CONSTRAINT pk_users_providers PRIMARY KEY (user_id, provider_id),
    CONSTRAINT fk_users_providers_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_users_providers_provider_id FOREIGN KEY (provider_id) REFERENCES providers (id) ON DELETE CASCADE
);