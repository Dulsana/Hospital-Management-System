-- Demo data for hospital_management.
-- Run database/schema-sqlserver.sql first, then run this script in SSMS.
USE hospital_management;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.departments WHERE name = N'Cardiology')
    INSERT INTO dbo.departments (name, description, icon_code, head_of_department) VALUES
    (N'Cardiology', N'Heart and vascular disease treatment unit', N'fa-heart-pulse', N'Dr. Anura Jayasinghe'),
    (N'Neurology', N'Brain and nervous system care', N'fa-brain', N'Dr. Sanduni Perera'),
    (N'Pediatrics', N'Care for infants and children', N'fa-baby', N'Dr. Chamari Wickramasinghe');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.doctors WHERE name = N'Dr. Anura Jayasinghe')
BEGIN
    DECLARE @cardiology BIGINT = (SELECT id FROM dbo.departments WHERE name = N'Cardiology');
    DECLARE @neurology BIGINT = (SELECT id FROM dbo.departments WHERE name = N'Neurology');
    INSERT INTO dbo.doctors (name, title, department_id, qualifications, fee, room_number, schedule_days, time_slot, email, phone) VALUES
    (N'Dr. Anura Jayasinghe', N'Senior Consultant Cardiologist', @cardiology, N'MBBS, MD Cardiology', 3500.00, N'Room 204', N'Mon, Wed, Fri', N'04:00 PM - 07:00 PM', N'anura@medicare.lk', N'0771112233'),
    (N'Dr. Sanduni Perera', N'Consultant Neurologist', @neurology, N'MBBS, MD Neurology', 3800.00, N'Room 108', N'Tue, Thu, Sat', N'05:00 PM - 08:00 PM', N'sanduni@medicare.lk', N'0772223344');
END
GO

IF NOT EXISTS (SELECT 1 FROM dbo.wards WHERE name = N'ICU')
    INSERT INTO dbo.wards (name, total_beds, occupied_beds, available_beds, ward_type) VALUES
    (N'ICU', 20, 16, 4, N'Critical'),
    (N'General Ward', 40, 25, 15, N'General'),
    (N'Pediatric Ward', 25, 18, 7, N'Specialized');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.operation_theatres WHERE id = N'OT-1')
    INSERT INTO dbo.operation_theatres (id, suite_name, lead_surgeon, current_procedure, status, scheduled_time) VALUES
    (N'OT-1', N'General Surgery Suite', N'Dr. Nishantha Silva', N'Laparoscopic Surgery', N'SCHEDULED', N'08:00 AM - 11:30 AM'),
    (N'OT-2', N'Cardiac OT Suite', N'Dr. Anura Jayasinghe', N'Cardiac Procedure', N'READY', N'12:00 PM - 04:00 PM');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.pharmacy_medicines WHERE medicine_code = N'MED-001')
    INSERT INTO dbo.pharmacy_medicines (medicine_code, name, category, stock_quantity, unit_price) VALUES
    (N'MED-001', N'Paracetamol 500mg', N'Analgesic', 1250, 15.00),
    (N'MED-002', N'Amoxicillin 500mg', N'Antibiotic', 450, 45.00),
    (N'MED-003', N'Metformin 500mg', N'Diabetic Care', 800, 25.00);
GO

IF NOT EXISTS (SELECT 1 FROM dbo.blood_bank_inventory WHERE blood_group = N'A+')
    INSERT INTO dbo.blood_bank_inventory (blood_group, available_units, status) VALUES
    (N'A+', 28, N'AVAILABLE'), (N'A-', 8, N'LOW STOCK'),
    (N'B+', 35, N'AVAILABLE'), (N'O+', 42, N'AVAILABLE'), (N'O-', 14, N'UNIVERSAL DONOR');
GO

IF NOT EXISTS (SELECT 1 FROM dbo.health_packages WHERE name = N'Executive Health Checkup')
    INSERT INTO dbo.health_packages (name, price, is_popular, description) VALUES
    (N'Executive Health Checkup', 18500.00, 0, N'Complete annual screening package'),
    (N'Comprehensive Cardiac Shield', 28000.00, 1, N'Advanced heart health screening'),
    (N'Senior Citizen Care Package', 22000.00, 0, N'Preventive care for senior citizens');
GO
