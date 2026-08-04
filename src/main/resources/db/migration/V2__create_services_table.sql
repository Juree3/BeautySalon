CREATE TABLE services (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id          BIGINT NOT NULL REFERENCES users(id),
    name              VARCHAR(255) NOT NULL,
    description       VARCHAR(255),
    duration_minutes  INT NOT NULL,
    price             DECIMAL(10,2) NOT NULL,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    image_url         VARCHAR(500)
);