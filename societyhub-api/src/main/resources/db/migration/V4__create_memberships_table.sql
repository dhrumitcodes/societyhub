CREATE TABLE IF NOT EXISTS public.memberships (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  user_id BIGINT NOT NULL,
                                                  society_id BIGINT NOT NULL,
                                                  society_role VARCHAR(50) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES public.users (id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_society FOREIGN KEY (society_id) REFERENCES public.societies (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_society UNIQUE (user_id, society_id)
    );