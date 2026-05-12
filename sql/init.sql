CREATE DATABASE IF NOT EXISTS green_energy_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE green_energy_db;

-- =============================================
-- 1. users
-- =============================================
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    is_active     TINYINT(1) NOT NULL DEFAULT 1,
    created_by    BIGINT,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 2. annual_targets
-- =============================================
CREATE TABLE annual_targets (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_year            INT NOT NULL,
    annual_electricity_kwh DECIMAL(15, 4) NOT NULL,
    re100_target_ratio     DECIMAL(5, 4) NOT NULL,
    growth_rate            DECIMAL(5, 4) NOT NULL DEFAULT 0,
    created_by             BIGINT NOT NULL,
    updated_by             BIGINT,
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted             TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_target_year (target_year)
);

-- =============================================
-- 3. solar_devices
-- =============================================
CREATE TABLE solar_devices (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_name   VARCHAR(100) NOT NULL,
    capacity_kw   DECIMAL(10, 4) NOT NULL,
    install_date  DATE NOT NULL,
    location      VARCHAR(200),
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    location_id   BIGINT DEFAULT NULL,
    created_by    BIGINT NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 4. solar_monthly_records
-- =============================================
CREATE TABLE solar_monthly_records (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id       BIGINT NOT NULL,
    record_year     INT NOT NULL,
    record_month    INT NOT NULL,
    actual_kwh      DECIMAL(15, 4) NOT NULL,
    theoretical_kwh DECIMAL(15, 4),
    source          VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    created_by      BIGINT NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted      TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_device_month (device_id, record_year, record_month)
);

-- =============================================
-- 5. contracts
-- =============================================
CREATE TABLE contracts (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name          VARCHAR(100) NOT NULL,
    contract_type          VARCHAR(20) NOT NULL,
    monthly_supply_kwh     DECIMAL(15, 4) NOT NULL,
    start_date             DATE NOT NULL,
    end_date               DATE NOT NULL,
    rate_per_kwh           DECIMAL(15, 4) NOT NULL,
    monthly_cost_snapshot  DECIMAL(15, 4) NOT NULL,
    status                 VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    terminated_at          DATETIME DEFAULT NULL,
    terminated_by          BIGINT DEFAULT NULL,
    notes                  TEXT DEFAULT NULL,
    location_id            BIGINT DEFAULT NULL,
    created_by             BIGINT NOT NULL,
    updated_by             BIGINT DEFAULT NULL,
    created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted             TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 6. procurements
-- =============================================
CREATE TABLE procurements (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name       VARCHAR(100) NOT NULL,
    quantity            INT NOT NULL,
    kwh_equivalent      DECIMAL(15, 4) NOT NULL,
    certificate_year    INT NOT NULL,
    unit_price          DECIMAL(15, 4) NOT NULL,
    total_amount        DECIMAL(15, 4) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    expected_date       DATE DEFAULT NULL,
    completed_date      DATE DEFAULT NULL,
    purchase_month      VARCHAR(7) DEFAULT NULL,
    expiry_date         DATE DEFAULT NULL,
    is_void             TINYINT(1) NOT NULL DEFAULT 0,
    delivered           TINYINT(1) DEFAULT NULL,
    notes               TEXT DEFAULT NULL,
    supply_type         VARCHAR(20) NOT NULL DEFAULT 'REC_ONLY',
    location_id         BIGINT DEFAULT NULL,
    created_by          BIGINT NOT NULL,
    updated_by          BIGINT DEFAULT NULL,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted          TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 7. procurement_presets
-- =============================================
CREATE TABLE procurement_presets (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    label        VARCHAR(50) NOT NULL,
    quantity     INT NOT NULL,
    is_active    TINYINT(1) NOT NULL DEFAULT 1,
    created_by   BIGINT NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted   TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 8. electricity_usage_records
-- =============================================
CREATE TABLE electricity_usage_records (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_year   INT NOT NULL,
    record_month  INT NOT NULL,
    usage_kwh     DECIMAL(15, 4) NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    locked_at     DATETIME DEFAULT NULL,
    locked_by     BIGINT DEFAULT NULL,
    location_id   BIGINT DEFAULT NULL,
    created_by    BIGINT NOT NULL,
    updated_by    BIGINT DEFAULT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0,
    UNIQUE KEY uk_year_month (record_year, record_month)
);

-- =============================================
-- 9. monthly_summary_snapshots
-- =============================================
CREATE TABLE monthly_summary_snapshots (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_year         INT NOT NULL,
    record_month        INT NOT NULL,
    total_green_kwh     DECIMAL(15, 4) NOT NULL,
    solar_kwh           DECIMAL(15, 4) NOT NULL,
    contract_kwh        DECIMAL(15, 4) NOT NULL,
    procurement_kwh     DECIMAL(15, 4) NOT NULL,
    usage_kwh           DECIMAL(15, 4) NOT NULL,
    achievement_rate    DECIMAL(5, 4) NOT NULL,
    surplus_kwh         DECIMAL(15, 4) NOT NULL DEFAULT 0,
    locked_by           BIGINT NOT NULL,
    locked_at           DATETIME NOT NULL,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_year_month (record_year, record_month)
);

-- =============================================
-- 10. notifications
-- =============================================
CREATE TABLE notifications (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    type          VARCHAR(50) NOT NULL,
    title         VARCHAR(200) NOT NULL,
    message       TEXT NOT NULL,
    recipient_id  BIGINT NOT NULL,
    is_read       TINYINT(1) NOT NULL DEFAULT 0,
    ref_id        BIGINT DEFAULT NULL,
    ref_type      VARCHAR(50) DEFAULT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted    TINYINT(1) NOT NULL DEFAULT 0
);

-- =============================================
-- 11. audit_logs
-- =============================================
CREATE TABLE audit_logs (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    action        VARCHAR(50) NOT NULL,
    target_table  VARCHAR(100) NOT NULL,
    target_id     BIGINT NOT NULL,
    before_value  TEXT DEFAULT NULL,
    after_value   TEXT DEFAULT NULL,
    operator_id   BIGINT NOT NULL,
    operator_name VARCHAR(100) NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 初始資料：ADMIN 帳號
-- =============================================
INSERT INTO users (username, password, display_name, role, is_active, created_by)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE5cs8G.06mJ0VE0K',
    '系統管理者',
    'ADMIN',
    1,
    NULL
);
