CREATE TYPE auth_provider_type AS ENUM (
    'GOOGLE',
    'GITHUB',
    'EMAIL'
);

CREATE TABLE IF NOT EXISTS auth_providers (
    id SERIAL,
    auth_provider_type auth_provider_type NOT NULL,
    CONSTRAINT pk_providers PRIMARY KEY (id)
);

/*
    We can't have any constraints like VARCHAR(50) for name or email, because we are getting those values from the
    providers
 */
CREATE TABLE IF NOT EXISTS users_auth_providers (
    user_id INTEGER NOT NULL,
    auth_provider_id INTEGER NOT NULL,
    auth_provider_user_id TEXT NOT NULL,
    auth_provider_email TEXT NOT NULL,
    auth_provider_name TEXT NOT NULL,
    enabled BOOLEAN NOT NULL,
    CONSTRAINT pk_users_providers PRIMARY KEY (user_id, auth_provider_id),
    CONSTRAINT fk_users_providers_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_users_providers_provider_id FOREIGN KEY (auth_provider_id) REFERENCES auth_providers (id) ON DELETE CASCADE
);