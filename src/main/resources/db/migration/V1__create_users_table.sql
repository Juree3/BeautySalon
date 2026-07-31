CREATE TABLE users (
        id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        full_name VARCHAR(255) NOT NULL,
        email  VARCHAR(255) NOT NULL UNIQUE,
        password_hash VARCHAR(255),
        role VARCHAR(20) NOT NULL,
        phone VARCHAR(20),
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);