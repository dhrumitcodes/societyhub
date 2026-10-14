CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        society_id BIGINT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        location VARCHAR(255),
                        start_time TIMESTAMP NOT NULL,
                        end_time TIMESTAMP NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_event_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE
);