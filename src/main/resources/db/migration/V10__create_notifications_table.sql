CREATE TABLE notifications (
                               id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               user_id     BIGINT NOT NULL,
                               message     VARCHAR(500) NOT NULL,
                               read        BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

                               CONSTRAINT fk_notifications_user FOREIGN KEY (user_id)
                                   REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user ON notifications(user_id);