-- OAuth2 Authorization Server Tables

-- Create oauth2_authorization table for storing authorization codes, access tokens, and refresh tokens
CREATE TABLE oauth2_authorization (
    id VARCHAR(100) PRIMARY KEY,
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorization_grant_type VARCHAR(100) NOT NULL,
    authorized_scopes TEXT,
    attributes TEXT,
    state VARCHAR(500),
    authorization_code_value TEXT,
    authorization_code_issued_at TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata TEXT,
    access_token_value TEXT,
    access_token_issued_at TIMESTAMP,
    access_token_expires_at TIMESTAMP,
    access_token_metadata TEXT,
    access_token_type VARCHAR(100),
    access_token_scopes TEXT,
    oidc_id_token_value TEXT,
    oidc_id_token_issued_at TIMESTAMP,
    oidc_id_token_expires_at TIMESTAMP,
    oidc_id_token_metadata TEXT,
    refresh_token_value TEXT,
    refresh_token_issued_at TIMESTAMP,
    refresh_token_expires_at TIMESTAMP,
    refresh_token_metadata TEXT,
    user_code_value TEXT,
    user_code_issued_at TIMESTAMP,
    user_code_expires_at TIMESTAMP,
    user_code_metadata TEXT,
    device_code_value TEXT,
    device_code_issued_at TIMESTAMP,
    device_code_expires_at TIMESTAMP,
    device_code_metadata TEXT
);

-- Create oauth2_authorization_consent table for storing authorization consents
CREATE TABLE oauth2_authorization_consent (
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorities TEXT NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

-- Create oauth2_registered_client table for storing OAuth2 clients
CREATE TABLE oauth2_registered_client (
    id VARCHAR(100) PRIMARY KEY,
    client_id VARCHAR(100) NOT NULL,
    client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret VARCHAR(200),
    client_secret_expires_at TIMESTAMP,
    client_name VARCHAR(200) NOT NULL,
    client_authentication_methods TEXT NOT NULL,
    authorization_grant_types TEXT NOT NULL,
    redirect_uris TEXT,
    post_logout_redirect_uris TEXT,
    scopes TEXT NOT NULL,
    client_settings TEXT NOT NULL,
    token_settings TEXT NOT NULL
);

-- Create indexes for OAuth2 tables
CREATE INDEX idx_oauth2_authorization_registered_client_id ON oauth2_authorization(registered_client_id);
CREATE INDEX idx_oauth2_authorization_principal_name ON oauth2_authorization(principal_name);
CREATE INDEX idx_oauth2_registered_client_client_id ON oauth2_registered_client(client_id);

-- Insert default OAuth2 client for the K-Store application
INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_secret,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    redirect_uris,
    scopes,
    client_settings,
    token_settings
) VALUES (
    'k-store-client-id',
    'k-store-client',
    '$2a$10$k2l/t9bKKp4LqHv4qzYrVeDCNzBgT5VPdX1X1X1X1X1X1X1X1X1X1X1', -- encoded: k-store-secret
    'K-Store Application',
    'client_secret_basic,client_secret_post',
    'authorization_code,refresh_token,client_credentials',
    'http://localhost:8080/login/oauth2/code/k-store,http://localhost:3000/callback',
    'openid,profile,email,read,write',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.refresh-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"]}'
);

-- Insert API Gateway client for service-to-service communication
INSERT INTO oauth2_registered_client (
    id,
    client_id,
    client_secret,
    client_name,
    client_authentication_methods,
    authorization_grant_types,
    scopes,
    client_settings,
    token_settings
) VALUES (
    'api-gateway-client-id',
    'api-gateway',
    '$2a$10$gateway123encodedpassword', -- encoded: gateway-secret
    'API Gateway Service',
    'client_secret_basic',
    'client_credentials',
    'read,write,admin',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":false}',
    '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.access-token-time-to-live":["java.time.Duration",3600.000000000]}'
);
