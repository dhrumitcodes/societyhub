CREATE TABLE announcements (
                               id BIGSERIAL PRIMARY KEY,
                               society_id BIGINT NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               content TEXT NOT NULL,
                               priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_announcement_society FOREIGN KEY (society_id) REFERENCES societies(id) ON DELETE CASCADE
);