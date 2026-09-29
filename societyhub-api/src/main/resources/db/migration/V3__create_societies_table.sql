CREATE TABLE IF NOT EXISTS public.societies (
                                                id BIGSERIAL PRIMARY KEY,
                                                name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    category VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );