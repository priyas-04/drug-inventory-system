-- ============================================================
-- Drug Inventory System - Sample Seed Data
-- Run AFTER schema.sql to populate the database with test data
-- Default passwords are all: admin123 (BCrypt hashed)
-- ============================================================

USE drug_inventory_db;

-- -------------------------------------------------------
-- USERS (password = "admin123" BCrypt hashed)
-- -------------------------------------------------------
INSERT IGNORE INTO users (username, password, email, full_name, phone, role, active) VALUES
('admin',       '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6/0AT7.bVfSAiM4m', 'admin@drugstore.com',      'System Admin',         '9876543210', 'ADMIN',       TRUE),
('pharmacist1', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6/0AT7.bVfSAiM4m', 'pharmacist@drugstore.com',  'Sarah Johnson',        '9876543211', 'PHARMACIST',  TRUE),
('supplier1',   '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6/0AT7.bVfSAiM4m', 'supplier@medcorp.com',      'Medical Corp Contact', '9876543212', 'SUPPLIER',    TRUE),
('pharmacist2', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6/0AT7.bVfSAiM4m', 'pharmacist2@drugstore.com', 'David Chen',           '9876543213', 'PHARMACIST',  TRUE);

-- -------------------------------------------------------
-- SUPPLIERS
-- -------------------------------------------------------
INSERT IGNORE INTO suppliers (name, email, phone, address, license_number, contact_person, active) VALUES
('MedCorp Pharmaceuticals', 'sales@medcorp.com',    '1234567890', '123 Pharma Street, Mumbai',    'LIC-MC-001', 'Rajiv Sharma',  TRUE),
('HealthPlus Distributors', 'orders@healthplus.com', '1234567891', '456 Medical Lane, Delhi',      'LIC-HP-002', 'Priya Patel',   TRUE),
('PharmaLink Supplies',     'supply@pharmalink.com', '1234567892', '789 Drug Avenue, Chennai',     'LIC-PL-003', 'Kumar Reddy',   TRUE),
('Global Meds Ltd',         'global@globalmeds.com', '1234567893', '321 Health Blvd, Bangalore',   'LIC-GM-004', 'Anita Singh',   TRUE),
('CarePlus Wholesale',      'care@careplus.com',     '1234567894', '654 Wellness Road, Hyderabad', 'LIC-CP-005', 'Mohan Das',     TRUE);

-- -------------------------------------------------------
-- DRUGS (mix of normal, low-stock, and expired for demo)
-- -------------------------------------------------------
INSERT IGNORE INTO drugs (name, batch_number, expiry_date, manufacture_date, quantity, price, category, manufacturer, description, low_stock_threshold, supplier_id, active) VALUES
-- Normal stock (good expiry)
('Amoxicillin 500mg',     'BATCH-AMX-001', '2027-06-30', '2024-06-01',  200, 12.50,  'Antibiotic',    'MedCorp Pharma',  'Broad-spectrum antibiotic',             20, 1, TRUE),
('Ibuprofen 400mg',       'BATCH-IBU-002', '2027-03-31', '2024-03-01',  350, 8.00,   'Painkiller',    'HealthPlus',      'Anti-inflammatory pain reliever',        30, 2, TRUE),
('Metformin 500mg',       'BATCH-MET-003', '2026-12-31', '2023-12-01',  500, 15.00,  'Antidiabetic',  'PharmaLink',      'Controls blood sugar levels',           50, 3, TRUE),
('Atorvastatin 20mg',     'BATCH-ATV-004', '2027-09-30', '2024-09-01',  400, 22.00,  'Statin',        'Global Meds',     'Reduces cholesterol',                   25, 4, TRUE),
('Omeprazole 20mg',       'BATCH-OMP-005', '2027-01-31', '2024-01-01',  300, 18.00,  'Antacid',       'CarePlus',        'Treats acid reflux and ulcers',         20, 5, TRUE),
('Paracetamol 500mg',     'BATCH-PAR-006', '2027-05-31', '2024-05-01',  600, 5.50,   'Painkiller',    'MedCorp Pharma',  'Fever and pain relief',                 50, 1, TRUE),
('Cetirizine 10mg',       'BATCH-CET-007', '2026-11-30', '2023-11-01',  250, 9.00,   'Antihistamine', 'HealthPlus',      'Allergy medication',                    20, 2, TRUE),
('Azithromycin 500mg',    'BATCH-AZI-008', '2027-08-31', '2024-08-01',  180, 35.00,  'Antibiotic',    'PharmaLink',      'Treats respiratory infections',          15, 3, TRUE),

-- LOW STOCK (quantity below threshold for alert demo)
('Insulin Regular 10ml',  'BATCH-INS-009', '2026-07-31', '2024-01-15',   5, 85.00,  'Antidiabetic',  'Global Meds',     'Fast-acting insulin injection',         10, 4, TRUE),
('Morphine 10mg',         'BATCH-MOR-010', '2026-10-31', '2024-04-01',   3, 120.00, 'Analgesic',     'CarePlus',        'Strong opioid pain medication',          5, 5, TRUE),

-- EXPIRED (for expiry tracking demo)
('Aspirin 75mg',          'BATCH-ASP-011', '2024-12-31', '2022-12-01',  50, 6.00,   'Painkiller',    'MedCorp Pharma',  'Blood thinner and pain reliever',       30, 1, TRUE),
('Vitamin C 500mg',       'BATCH-VTC-012', '2025-01-15', '2023-01-15', 100, 11.00,  'Supplement',    'HealthPlus',      'Immune system support',                 20, 2, TRUE);

-- -------------------------------------------------------
-- INVENTORY (stock levels at different locations)
-- -------------------------------------------------------
INSERT IGNORE INTO inventory (drug_id, location, quantity, low_stock_threshold) VALUES
-- WAREHOUSE stock
(1,  'WAREHOUSE', 150, 20),
(2,  'WAREHOUSE', 250, 30),
(3,  'WAREHOUSE', 400, 50),
(4,  'WAREHOUSE', 300, 25),
(5,  'WAREHOUSE', 200, 20),
(6,  'WAREHOUSE', 400, 50),
(7,  'WAREHOUSE', 180, 20),
(8,  'WAREHOUSE', 120, 15),
(9,  'WAREHOUSE',   3, 10),  -- LOW STOCK
(10, 'WAREHOUSE',   2,  5),  -- LOW STOCK
(11, 'WAREHOUSE',  50, 30),  -- EXPIRED
(12, 'WAREHOUSE', 100, 20),  -- EXPIRED
-- PHARMACY stock
(1,  'PHARMACY',  50, 20),
(2,  'PHARMACY', 100, 30),
(3,  'PHARMACY', 100, 50),
(6,  'PHARMACY', 200, 50),
-- HOSPITAL stock
(1,  'HOSPITAL',  30, 20),
(4,  'HOSPITAL', 100, 25),
(5,  'HOSPITAL', 100, 20);

-- -------------------------------------------------------
-- PURCHASE ORDERS
-- -------------------------------------------------------
INSERT IGNORE INTO purchase_orders (supplier_id, order_date, delivery_date, status, notes, created_by) VALUES
(1, '2026-03-10 09:00:00', '2026-03-13 14:00:00', 'DELIVERED', 'Monthly restock of antibiotics',    1),
(2, '2026-03-12 10:00:00', NULL,                  'APPROVED',  'Painkiller restock order',           1),
(3, '2026-03-15 11:00:00', NULL,                  'PENDING',   'Antidiabetic drug order',            2),
(4, '2026-03-16 08:00:00', NULL,                  'CANCELLED', 'Cancelled - supplier unavailable',   1);

-- -------------------------------------------------------
-- PURCHASE ORDER ITEMS
-- -------------------------------------------------------
INSERT IGNORE INTO purchase_order_items (purchase_order_id, drug_id, quantity, unit_price) VALUES
(1, 1, 100, 10.00),  -- Order 1: Amoxicillin
(1, 8,  50, 30.00),  -- Order 1: Azithromycin
(2, 2, 200,  7.00),  -- Order 2: Ibuprofen
(2, 6, 300,  4.50),  -- Order 2: Paracetamol
(3, 3, 200, 13.00),  -- Order 3: Metformin
(3, 9,  20, 80.00);  -- Order 3: Insulin (restock low stock)

-- -------------------------------------------------------
-- SAMPLE TRANSACTIONS (audit trail)
-- -------------------------------------------------------
INSERT IGNORE INTO transactions (drug_id, type, quantity, transaction_date, notes, performed_by, reference_id) VALUES
(1, 'PURCHASE',   100, '2026-03-13 14:00:00', 'Purchase Order #1 delivered',      1, 1),
(8, 'PURCHASE',    50, '2026-03-13 14:00:00', 'Purchase Order #1 delivered',      1, 1),
(6, 'SALE',        20, '2026-03-14 09:30:00', 'Dispensed to patient',             2, NULL),
(1, 'SALE',        10, '2026-03-14 10:00:00', 'Dispensed to patient',             2, NULL),
(2, 'SALE',        15, '2026-03-15 11:00:00', 'Dispensed to patient',             4, NULL),
(1, 'TRANSFER',    50, '2026-03-14 08:00:00', 'Warehouse to Pharmacy restock',    1, NULL),
(6, 'TRANSFER',   200, '2026-03-14 08:30:00', 'Warehouse to Pharmacy restock',    1, NULL);

-- -------------------------------------------------------
-- STOCK MOVEMENTS
-- -------------------------------------------------------
INSERT IGNORE INTO stock_movements (drug_id, from_location, to_location, quantity, moved_at, reason, moved_by) VALUES
(1, 'WAREHOUSE', 'PHARMACY', 50, '2026-03-14 08:00:00', 'Weekly pharmacy restock', 1),
(6, 'WAREHOUSE', 'PHARMACY', 200,'2026-03-14 08:30:00', 'Weekly pharmacy restock', 1),
(4, 'WAREHOUSE', 'HOSPITAL', 100,'2026-03-15 09:00:00', 'Hospital monthly supply', 1),
(5, 'WAREHOUSE', 'HOSPITAL', 100,'2026-03-15 09:30:00', 'Hospital monthly supply', 1);
