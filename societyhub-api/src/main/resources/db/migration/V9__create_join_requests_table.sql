CREATE TABLE join_requests (
                               id          BIGSERIAL PRIMARY KEY,
                               user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                               society_id  BIGINT       NOT NULL REFERENCES societies (id) ON DELETE CASCADE,
                               status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                               message     VARCHAR(500),
                               created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               decided_at  TIMESTAMP
);

CREATE UNIQUE INDEX uq_join_requests_pending
    ON join_requests (user_id, society_id)
    WHERE status = 'PENDING';

CREATE INDEX idx_join_requests_society_status ON join_requests (society_id, status);