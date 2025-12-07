CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    city_code VARCHAR(16) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    event_time TIMESTAMP NOT NULL,
    price NUMERIC(18,2) NOT NULL,
    type VARCHAR(32) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transactions_event_time ON transactions(event_time);
CREATE INDEX IF NOT EXISTS idx_transactions_city_time ON transactions(city_code, event_time);
