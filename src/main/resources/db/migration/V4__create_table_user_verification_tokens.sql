CREATE TABLE IF NOT EXISTS user_activation_tokens (
    id SERIAL NOT NULL,
    token_value TEXT NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_user_activation_tokens PRIMARY KEY (id),
    CONSTRAINT fk_user_activation_tokens_user_id FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);