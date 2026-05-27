-- Initial baseline schema for template usage.
-- This migration creates the current persistence model so environments can
-- start from migration-based schema management instead of hibernate auto-ddl.

CREATE TABLE IF NOT EXISTS categories (
    "Number" VARCHAR(255) PRIMARY KEY,
    row_id INTEGER GENERATED ALWAYS AS IDENTITY UNIQUE,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    locked_at TIMESTAMP,
    locked_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    row_version BIGINT,
    modification_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id BIGINT,
    slug VARCHAR(255),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
    "Number" VARCHAR(255) PRIMARY KEY,
    row_id INTEGER GENERATED ALWAYS AS IDENTITY UNIQUE,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    last_modified_at TIMESTAMP,
    last_modified_by VARCHAR(255),
    locked_at TIMESTAMP,
    locked_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    row_version BIGINT,
    modification_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price NUMERIC(19,2) NOT NULL,
    stock INTEGER NOT NULL,
    category_id BIGINT,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    audit_type VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    user_id BIGINT,
    username VARCHAR(255),
    service_name VARCHAR(100) NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    old_value TEXT,
    new_value TEXT,
    metadata TEXT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    stack_trace TEXT,
    severity VARCHAR(20),
    timestamp TIMESTAMP NOT NULL,
    duration_ms BIGINT,
    correlation_id VARCHAR(100),
    session_id VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS application_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    logger VARCHAR(255),
    message TEXT,
    thread VARCHAR(100),
    method VARCHAR(255),
    class_name VARCHAR(255),
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    file_name VARCHAR(255),
    line_number INTEGER,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50),
    shift_id VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS access_logs (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    endpoint VARCHAR(500),
    path VARCHAR(500),
    query_string VARCHAR(1000),
    request_id VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    referer VARCHAR(500),
    origin VARCHAR(255),
    request_size INTEGER,
    status_code INTEGER,
    response_size INTEGER,
    content_type VARCHAR(100),
    response_time_ms BIGINT,
    request_time TIMESTAMP,
    response_time TIMESTAMP,
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    auth_method VARCHAR(50),
    authenticated BOOLEAN DEFAULT FALSE,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS error_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    exception_type VARCHAR(255),
    message TEXT,
    stack_trace TEXT,
    root_cause TEXT,
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    class_name VARCHAR(255),
    method VARCHAR(255),
    file_name VARCHAR(255),
    line_number INTEGER,
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    error_code VARCHAR(50),
    category VARCHAR(100),
    resolved BOOLEAN DEFAULT FALSE,
    resolution TEXT,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS performance_logs (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    operation_type VARCHAR(50),
    duration_ms BIGINT,
    threshold_ms BIGINT,
    is_slow BOOLEAN DEFAULT FALSE,
    memory_used_mb BIGINT,
    cpu_percent DOUBLE PRECISION,
    thread_count INTEGER,
    sql_query TEXT,
    query_time_ms BIGINT,
    rows_affected INTEGER,
    connection_pool_size INTEGER,
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    status_code INTEGER,
    user_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_audit_type ON audit_logs (audit_type);
CREATE INDEX IF NOT EXISTS idx_entity_type ON audit_logs (entity_type);
CREATE INDEX IF NOT EXISTS idx_user_id ON audit_logs (user_id);
CREATE INDEX IF NOT EXISTS idx_timestamp ON audit_logs (timestamp);
CREATE INDEX IF NOT EXISTS idx_service_name ON audit_logs (service_name);

CREATE INDEX IF NOT EXISTS idx_app_level ON application_logs (level);
CREATE INDEX IF NOT EXISTS idx_app_service ON application_logs (service_name);
CREATE INDEX IF NOT EXISTS idx_app_timestamp ON application_logs (timestamp);
CREATE INDEX IF NOT EXISTS idx_app_correlation ON application_logs (correlation_id);
CREATE INDEX IF NOT EXISTS idx_app_user ON application_logs (user_id);

CREATE INDEX IF NOT EXISTS idx_access_service ON access_logs (service_name);
CREATE INDEX IF NOT EXISTS idx_access_method ON access_logs (http_method);
CREATE INDEX IF NOT EXISTS idx_access_status ON access_logs (status_code);
CREATE INDEX IF NOT EXISTS idx_access_timestamp ON access_logs (timestamp);
CREATE INDEX IF NOT EXISTS idx_access_user ON access_logs (user_id);
CREATE INDEX IF NOT EXISTS idx_access_ip ON access_logs (ip_address);
CREATE INDEX IF NOT EXISTS idx_access_endpoint ON access_logs (endpoint);

CREATE INDEX IF NOT EXISTS idx_error_level ON error_logs (level);
CREATE INDEX IF NOT EXISTS idx_error_service ON error_logs (service_name);
CREATE INDEX IF NOT EXISTS idx_error_type ON error_logs (exception_type);
CREATE INDEX IF NOT EXISTS idx_error_timestamp ON error_logs (timestamp);
CREATE INDEX IF NOT EXISTS idx_error_correlation ON error_logs (correlation_id);
CREATE INDEX IF NOT EXISTS idx_error_category ON error_logs (category);
CREATE INDEX IF NOT EXISTS idx_error_resolved ON error_logs (resolved);

CREATE INDEX IF NOT EXISTS idx_perf_service ON performance_logs (service_name);
CREATE INDEX IF NOT EXISTS idx_perf_operation ON performance_logs (operation);
CREATE INDEX IF NOT EXISTS idx_perf_type ON performance_logs (operation_type);
CREATE INDEX IF NOT EXISTS idx_perf_timestamp ON performance_logs (timestamp);
CREATE INDEX IF NOT EXISTS idx_perf_slow ON performance_logs (is_slow);
CREATE INDEX IF NOT EXISTS idx_perf_duration ON performance_logs (duration_ms);
