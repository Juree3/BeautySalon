CREATE TABLE booking_items (
                               id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               booking_id        BIGINT NOT NULL,
                               service_id        BIGINT,
                               service_name      VARCHAR(255) NOT NULL,
                               price             DECIMAL(10,2) NOT NULL,
                               duration_minutes  INT NOT NULL,

                               CONSTRAINT fk_booking_items_booking FOREIGN KEY (booking_id)
                                   REFERENCES bookings(id) ON DELETE CASCADE,

                               CONSTRAINT fk_booking_items_service FOREIGN KEY (service_id)
                                   REFERENCES services(id) ON DELETE SET NULL
);