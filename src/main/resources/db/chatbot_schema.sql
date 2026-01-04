-- SQL script to create chatbot tables
-- Run this script manually on the database before deploying the chatbot feature

-- Chat session table
CREATE TABLE IF NOT EXISTS chat_session (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(36) UNIQUE NOT NULL,
    user_id BIGINT,
    vehicle_id BIGINT,
    conversation_json TEXT,
    extracted_data_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    offer_id BIGINT
);

-- Index for faster lookups
CREATE INDEX IF NOT EXISTS idx_chat_session_session_id ON chat_session(session_id);
CREATE INDEX IF NOT EXISTS idx_chat_session_user_id ON chat_session(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_session_status ON chat_session(status);
CREATE INDEX IF NOT EXISTS idx_chat_session_expires_at ON chat_session(expires_at);

-- System configuration table
CREATE TABLE IF NOT EXISTS system_configuration (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(500),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default configuration
INSERT INTO system_configuration (config_key, config_value, description) VALUES
    ('chatbot.enabled', 'true', 'Enable or disable AI chatbot feature'),
    ('chatbot.margin.percent', '30', 'Default margin percentage for parts pricing'),
    ('chatbot.labor.rate', '150', 'Default labor rate per hour in PLN'),
    ('chatbot.session.timeout.minutes', '30', 'Session timeout in minutes'),
    ('ollama.model', 'PRIHLOP/PLLuM:Q4_K_M', 'Ollama model name to use')
ON CONFLICT (config_key) DO NOTHING;

-- Inter Cars API cache table (for Phase 3)
CREATE TABLE IF NOT EXISTS intercars_cache (
    id BIGSERIAL PRIMARY KEY,
    search_key VARCHAR(255) UNIQUE NOT NULL,
    response_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);

-- Index for cache lookups
CREATE INDEX IF NOT EXISTS idx_intercars_cache_search_key ON intercars_cache(search_key);
CREATE INDEX IF NOT EXISTS idx_intercars_cache_expires_at ON intercars_cache(expires_at);
