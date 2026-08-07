CREATE TABLE work_slots(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    staff_id        BIGINT NOT NULL REFERENCES users(id),
    date            DATE NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL
);