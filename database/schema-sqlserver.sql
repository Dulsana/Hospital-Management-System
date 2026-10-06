-- Medicare Hospital Management System - SQL Server schema
-- Run this script in SSMS before starting the backend with the sqlserver profile.

IF DB_ID(N'hospital_management') IS NULL
    CREATE DATABASE hospital_management;
GO

USE hospital_management;
GO

IF OBJECT_ID(N'dbo.departments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.departments (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE,
        description NVARCHAR(MAX) NULL,
        icon_code NVARCHAR(50) NULL DEFAULT N'fa-stethoscope',
        head_of_department NVARCHAR(100) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );
END
GO

IF OBJECT_ID(N'dbo.doctors', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.doctors (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        title NVARCHAR(100) NOT NULL,
        department_id BIGINT NULL,
        qualifications NVARCHAR(255) NULL,
        fee DECIMAL(10,2) NOT NULL,
        room_number NVARCHAR(100) NULL,
        schedule_days NVARCHAR(100) NULL,
        time_slot NVARCHAR(100) NULL,
        image_url NVARCHAR(255) NULL,
        email NVARCHAR(100) NULL,
        phone NVARCHAR(20) NULL,
        CONSTRAINT fk_doctors_department FOREIGN KEY (department_id) REFERENCES dbo.departments(id) ON DELETE SET NULL
    );
END
GO

IF OBJECT_ID(N'dbo.appointments', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.appointments (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        reference_code NVARCHAR(50) NOT NULL UNIQUE,
        doctor_id BIGINT NOT NULL,
        patient_name NVARCHAR(100) NOT NULL,
        patient_phone NVARCHAR(20) NOT NULL,
        patient_email NVARCHAR(100) NULL,
        appointment_date DATE NOT NULL,
        time_slot NVARCHAR(100) NOT NULL,
        status NVARCHAR(30) NULL DEFAULT N'CONFIRMED',
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES dbo.doctors(id) ON DELETE CASCADE
    );
END
GO

IF OBJECT_ID(N'dbo.wards', N'U') IS NULL
    CREATE TABLE dbo.wards (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(100) NOT NULL UNIQUE, total_beds INT NOT NULL, occupied_beds INT NULL DEFAULT 0, available_beds INT NOT NULL, ward_type NVARCHAR(50) NOT NULL);
GO

IF OBJECT_ID(N'dbo.operation_theatres', N'U') IS NULL
    CREATE TABLE dbo.operation_theatres (id NVARCHAR(20) PRIMARY KEY, suite_name NVARCHAR(100) NOT NULL, lead_surgeon NVARCHAR(100) NULL, current_procedure NVARCHAR(255) NULL, status NVARCHAR(30) NULL DEFAULT N'READY', scheduled_time NVARCHAR(100) NULL);
GO

IF OBJECT_ID(N'dbo.pharmacy_medicines', N'U') IS NULL
    CREATE TABLE dbo.pharmacy_medicines (id BIGINT IDENTITY(1,1) PRIMARY KEY, medicine_code NVARCHAR(50) NOT NULL UNIQUE, name NVARCHAR(100) NOT NULL, category NVARCHAR(100) NOT NULL, stock_quantity INT NOT NULL DEFAULT 0, unit_price DECIMAL(10,2) NOT NULL);
GO

IF OBJECT_ID(N'dbo.blood_bank_inventory', N'U') IS NULL
    CREATE TABLE dbo.blood_bank_inventory (id BIGINT IDENTITY(1,1) PRIMARY KEY, blood_group NVARCHAR(10) NOT NULL UNIQUE, available_units INT NOT NULL DEFAULT 0, status NVARCHAR(30) NULL DEFAULT N'AVAILABLE');
GO

IF OBJECT_ID(N'dbo.health_packages', N'U') IS NULL
    CREATE TABLE dbo.health_packages (id BIGINT IDENTITY(1,1) PRIMARY KEY, name NVARCHAR(255) NOT NULL, price DECIMAL(10,2) NOT NULL, is_popular BIT NULL, description NVARCHAR(MAX) NULL);
GO
