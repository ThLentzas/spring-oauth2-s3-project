CREATE TABLE IF NOT EXISTS social_accounts (
    id SERIAL,
    user_id INTEGER NOT NULL,
    account_link TEXT NOT NULL,
    CONSTRAINT pk_social_accounts PRIMARY KEY (id),
    CONSTRAINT fk_social_accounts_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);