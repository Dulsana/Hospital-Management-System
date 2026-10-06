-- ==========================================================================
-- MEDICARE HOSPITAL MANAGEMENT SYSTEM - MYSQL DATABASE SCHEMA (schema.sql)
-- Extended with 6 Core Branches: Ward, Appointment, Doctor Roster, OT, Pharmacy, Blood Bank
-- Database: medicare_hms_db
-- ==========================================================================

CREATE DATABASE IF NOT EXISTS medicare_hms_db;
USE medicare_hms_db;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    icon_code VARCHAR(50) DEFAULT 'fa-stethoscope',
    head_of_department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    department_id BIGINT,
    qualifications VARCHAR(255) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    room_number VARCHAR(100) NOT NULL,
    schedule_days VARCHAR(100) NOT NULL,
    time_slot VARCHAR(100) NOT NULL,
    image_url VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- 3. Doctor Schedules Table (Doctor Roster)
CREATE TABLE IF NOT EXISTS doctor_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time VARCHAR(20) NOT NULL,
    end_time VARCHAR(20) NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    status VARCHAR(30) DEFAULT 'AVAILABLE',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- 4. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    age INT,
    gender VARCHAR(20),
    nic_or_passport VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Appointments Table (Appointment Scheduling Branch)
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_code VARCHAR(50) NOT NULL UNIQUE,
    doctor_id BIGINT NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    patient_phone VARCHAR(20) NOT NULL,
    patient_email VARCHAR(100),
    appointment_date DATE NOT NULL,
    time_slot VARCHAR(100) NOT NULL,
    status VARCHAR(30) DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

-- 6. Wards & Beds Table (Ward & Bed Management Branch)
CREATE TABLE IF NOT EXISTS wards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    total_beds INT NOT NULL,
    occupied_beds INT DEFAULT 0,
    available_beds INT NOT NULL,
    ward_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bed_reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ward_id BIGINT NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    patient_phone VARCHAR(20) NOT NULL,
    reservation_date DATE NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ward_id) REFERENCES wards(id) ON DELETE CASCADE
);

-- 7. Operation Theatre (OT) Suites & Surgeries (OT Branch)
CREATE TABLE IF NOT EXISTS operation_theatres (
    id VARCHAR(20) PRIMARY KEY,
    suite_name VARCHAR(100) NOT NULL,
    lead_surgeon VARCHAR(100),
    current_procedure VARCHAR(255),
    status VARCHAR(30) DEFAULT 'READY',
    scheduled_time VARCHAR(100)
);

-- 8. Pharmacy Inventory (Pharmacy Management Branch)
CREATE TABLE IF NOT EXISTS pharmacy_medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    unit_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Blood Bank Inventory (Blood Bank Management Branch)
CREATE TABLE IF NOT EXISTS blood_bank_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blood_group VARCHAR(10) NOT NULL UNIQUE,
    available_units INT NOT NULL DEFAULT 0,
    status VARCHAR(30) DEFAULT 'AVAILABLE',
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS blood_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blood_group VARCHAR(10) NOT NULL,
    required_units INT NOT NULL,
    ward_or_location VARCHAR(100) NOT NULL,
    urgency VARCHAR(30) DEFAULT 'HIGH',
    status VARCHAR(30) DEFAULT 'DISPATCHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Health Screening Packages
CREATE TABLE IF NOT EXISTS health_packages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    is_popular BOOLEAN,
    description TEXT
);

-- ==========================================================================
-- PHARMACY MANAGEMENT MODULE SCHEMA (Comprehensive 9 Tables)
-- ==========================================================================

-- 1. Medicine Categories Table
CREATE TABLE IF NOT EXISTS medicine_categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Suppliers Table
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(150) NOT NULL UNIQUE,
    contact_person VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Medicines Table
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(150) NOT NULL UNIQUE,
    generic_name VARCHAR(150) NOT NULL,
    category_id BIGINT,
    supplier_id BIGINT,
    description TEXT,
    dosage_form VARCHAR(50) NOT NULL,
    strength VARCHAR(50) NOT NULL,
    unit VARCHAR(30) NOT NULL,
    selling_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    reorder_level INT NOT NULL DEFAULT 10,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES medicine_categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

-- 4. Medicine Stock (Batches) Table
CREATE TABLE IF NOT EXISTS medicine_stock (
    stock_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_id BIGINT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    expiry_date DATE NOT NULL,
    received_date DATE NOT NULL,
    supplier_id BIGINT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE SET NULL
);

-- 5. Stock Transactions History Table
CREATE TABLE IF NOT EXISTS stock_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    medicine_id BIGINT NOT NULL,
    stock_id BIGINT,
    transaction_type VARCHAR(30) NOT NULL, -- STOCK_IN, DISPENSE, ADJUSTMENT, RETURN, EXPIRED, DAMAGED
    quantity INT NOT NULL,
    reference_id VARCHAR(100),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    performed_by VARCHAR(100) DEFAULT 'System',
    notes TEXT,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES medicine_stock(stock_id) ON DELETE SET NULL
);

-- 6. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT,
    patient_name VARCHAR(100) NOT NULL,
    doctor_id BIGINT,
    doctor_name VARCHAR(100) NOT NULL,
    prescription_date DATE NOT NULL,
    diagnosis VARCHAR(255),
    notes TEXT,
    status VARCHAR(30) DEFAULT 'PENDING', -- PENDING, PARTIALLY_DISPENSED, DISPENSED, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE SET NULL
);

-- 7. Prescription Items Table
CREATE TABLE IF NOT EXISTS prescription_items (
    prescription_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    duration VARCHAR(50) NOT NULL,
    quantity_prescribed INT NOT NULL,
    instructions TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE
);

-- 8. Dispensings Table
CREATE TABLE IF NOT EXISTS dispensings (
    dispensing_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    pharmacist_name VARCHAR(100) DEFAULT 'Head Pharmacist',
    dispensing_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) DEFAULT 'DISPENSED',
    notes TEXT,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id) ON DELETE CASCADE
);

-- 9. Dispensing Items Table
CREATE TABLE IF NOT EXISTS dispensing_items (
    dispensing_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dispensing_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    stock_id BIGINT,
    quantity_dispensed INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (dispensing_id) REFERENCES dispensings(dispensing_id) ON DELETE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id) ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES medicine_stock(stock_id) ON DELETE SET NULL
);
