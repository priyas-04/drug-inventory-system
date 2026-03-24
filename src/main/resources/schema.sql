-- ============================================================
-- Drug Inventory & Supply Chain Management System
-- MySQL Database Schema
-- Run this AFTER creating the database:
--   CREATE DATABASE drug_inventory_db;
--   USE drug_inventory_db;
-- Note: With spring.jpa.hibernate.ddl-auto=update, tables are
--       auto-created. This script is for manual reference / review.
-- ============================================================

CREATE DATABASE IF NOT EXISTS drug_inventory_db;
USE drug_inventory_db;

-- -------------------------------------------------------
-- USERS table: stores all system users
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,           -- BCrypt hashed
    email      VARCHAR(100) NOT NULL UNIQUE,
    full_name  VARCHAR(100),
    phone      VARCHAR(15),
    role       ENUM('ADMIN','PHARMACIST','SUPPLIER') NOT NULL DEFAULT 'PHARMACIST',
    active     BOOLEAN NOT NULL DEFAULT TRUE
);

-- -------------------------------------------------------
-- SUPPLIERS table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS suppliers (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    email          VARCHAR(100) UNIQUE,
    phone          VARCHAR(15),
    address        VARCHAR(255),
    license_number VARCHAR(50) UNIQUE,
    contact_person VARCHAR(100),
    active         BOOLEAN NOT NULL DEFAULT TRUE
);

-- -------------------------------------------------------
-- DRUGS table: core medicines catalogue
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS drugs (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    batch_number        VARCHAR(50)  NOT NULL UNIQUE, -- Prevents duplicates
    expiry_date         DATE         NOT NULL,
    manufacture_date    DATE         NOT NULL,
    quantity            INT          NOT NULL DEFAULT 0,
    price               DECIMAL(10,2) NOT NULL,
    category            VARCHAR(100),
    manufacturer        VARCHAR(100),
    description         VARCHAR(255),
    low_stock_threshold INT          NOT NULL DEFAULT 10,
    supplier_id         BIGINT,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE SET NULL
);

-- -------------------------------------------------------
-- INVENTORY table: stock per drug per location
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS inventory (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id             BIGINT       NOT NULL,
    location            ENUM('WAREHOUSE','PHARMACY','HOSPITAL') NOT NULL,
    quantity            INT          NOT NULL DEFAULT 0,
    low_stock_threshold INT          NOT NULL DEFAULT 10,
    UNIQUE KEY uq_drug_location (drug_id, location),   -- One record per drug per location
    FOREIGN KEY (drug_id) REFERENCES drugs(id)
);

-- -------------------------------------------------------
-- TRANSACTIONS table: full audit trail
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id          BIGINT NOT NULL,
    type             ENUM('PURCHASE','SALE','TRANSFER','ADJUSTMENT','RETURN') NOT NULL,
    quantity         INT    NOT NULL,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes            VARCHAR(255),
    performed_by     BIGINT,
    reference_id     BIGINT,
    FOREIGN KEY (drug_id)      REFERENCES drugs(id),
    FOREIGN KEY (performed_by) REFERENCES users(id)
);

-- -------------------------------------------------------
-- PURCHASE_ORDERS table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_orders (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id   BIGINT NOT NULL,
    order_date    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivery_date DATETIME,
    status        ENUM('PENDING','APPROVED','DELIVERED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    notes         VARCHAR(255),
    created_by    BIGINT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    FOREIGN KEY (created_by)  REFERENCES users(id)
);

-- -------------------------------------------------------
-- PURCHASE_ORDER_ITEMS table
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS purchase_order_items (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT         NOT NULL,
    drug_id           BIGINT         NOT NULL,
    quantity          INT            NOT NULL,
    unit_price        DECIMAL(10,2)  NOT NULL,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (drug_id)           REFERENCES drugs(id)
);

-- -------------------------------------------------------
-- STOCK_MOVEMENTS table: tracks supply chain transfers
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS stock_movements (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    drug_id       BIGINT NOT NULL,
    from_location ENUM('WAREHOUSE','PHARMACY','HOSPITAL') NOT NULL,
    to_location   ENUM('WAREHOUSE','PHARMACY','HOSPITAL') NOT NULL,
    quantity      INT    NOT NULL,
    moved_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason        VARCHAR(255),
    moved_by      BIGINT,
    FOREIGN KEY (drug_id)  REFERENCES drugs(id),
    FOREIGN KEY (moved_by) REFERENCES users(id)
);
