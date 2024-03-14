CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id uuid DEFAULT uuid_generate_v4(),
    user_id INTEGER NOT NULL,
    token_value TEXT NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);