-- Create notification_inbox table for storing user notifications
CREATE TABLE notification_inbox (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_id BIGINT,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50),
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    is_read BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    archived_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_notification_inbox_user_id ON notification_inbox(user_id);
CREATE INDEX idx_notification_inbox_user_created ON notification_inbox(user_id, created_at DESC);
CREATE INDEX idx_notification_inbox_unread ON notification_inbox(user_id, is_read);
CREATE INDEX idx_notification_inbox_active ON notification_inbox(user_id, is_archived);
CREATE INDEX idx_notification_inbox_type ON notification_inbox(user_id, type);
CREATE INDEX idx_notification_inbox_priority ON notification_inbox(user_id, priority);
CREATE INDEX idx_notification_inbox_expires ON notification_inbox(expires_at);
