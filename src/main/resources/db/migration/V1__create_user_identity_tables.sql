CREATE TABLE users (
    id UUID PRIMARY KEY,
    password_hash VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_users_status
        CHECK (status IN ('UNVERIFIED', 'ACTIVE', 'DELETED'))
);

CREATE TABLE user_email_addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    email VARCHAR(254) NOT NULL,
    normalized_email VARCHAR(254) NOT NULL,
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_user_email_addresses_normalized_email
        UNIQUE (normalized_email),

    CONSTRAINT fk_user_email_addresses_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);