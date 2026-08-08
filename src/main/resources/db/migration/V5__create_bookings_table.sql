CREATE TABLE bookings (
                          id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          customer_id         BIGINT NOT NULL,
                          staff_id            BIGINT NOT NULL,
                          date                DATE NOT NULL,
                          start_time          TIME NOT NULL,
                          end_time            TIME NOT NULL,
                          status              VARCHAR(30) NOT NULL,
                          total_price         DECIMAL(10,2) NOT NULL,
                          total_duration_minutes INT NOT NULL,
                          created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),

                          CONSTRAINT fk_bookings_customer FOREIGN KEY (customer_id) REFERENCES users(id),
                          CONSTRAINT fk_bookings_staff FOREIGN KEY (staff_id) REFERENCES users(id),CONSTRAINT chk_bookings_customer_staff CHECK (customer_id != staff_id),

                CONSTRAINT chk_bookings_status CHECK (
                     status IN ('PENDING', 'CONFIRMED', 'COMPLETED',
                      'CANCELLED_BY_CUSTOMER', 'CANCELLED_BY_STAFF', 'NO_SHOW')
)
);

CREATE INDEX idx_bookings_staff_date ON bookings(staff_id, date);
CREATE INDEX idx_bookings_customer ON bookings(customer_id);