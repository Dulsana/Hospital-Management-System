-- ==========================================================================
-- MEDICARE HOSPITAL MANAGEMENT SYSTEM - MYSQL SEED DATA (data.sql)
-- Extended Seed Data for 6 Core Branches
-- Database: medicare_hms_db
-- ==========================================================================

USE medicare_hms_db;

-- 1. Insert Departments
INSERT INTO departments (id, name, description, icon_code, head_of_department) VALUES
(1, 'Cardiology', 'Heart, coronary & vascular disease treatment unit', 'fa-heart-pulse', 'Dr. Anura Jayasinghe'),
(2, 'Neurology', 'Brain, spinal cord & nerve disorders management', 'fa-brain', 'Dr. Sanduni Perera'),
(3, 'Orthopedics', 'Bone, joint replacement & musculoskeletal surgery', 'fa-bone', 'Dr. Nishantha Silva'),
(4, 'Pediatrics', 'Comprehensive infant, child & adolescent health care', 'fa-baby', 'Dr. Chamari Wickramasinghe'),
(5, 'Oncology', 'Cancer diagnostics, chemotherapy & radiation oncology', 'fa-ribbon', 'Dr. Kithsiri Fernando'),
(6, 'Gastroenterology', 'Digestive tract, liver & endoscopy services', 'fa-stethoscope', 'Dr. Priyantha Gunawardena')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Insert Doctors
INSERT INTO doctors (id, name, title, department_id, qualifications, fee, room_number, schedule_days, time_slot, image_url, email, phone) VALUES
(101, 'Dr. Anura Jayasinghe', 'Senior Consultant Cardiologist', 1, 'MBBS, MD (Cardiology), FRCP (UK)', 3500.00, 'Room 204, Main Building', 'Mon, Wed, Fri', '04:00 PM - 07:00 PM', 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?auto=format&fit=crop&w=600&q=80', 'anura.j@medicarehospital.lk', '0771112233'),
(102, 'Dr. Sanduni Perera', 'Consultant Neurologist', 2, 'MBBS, MD (Neurology), MRCP (UK)', 3800.00, 'Room 108, Specialist Wing', 'Tue, Thu, Sat', '05:00 PM - 08:00 PM', 'https://images.unsplash.com/photo-1594824813566-78a9956461a5?auto=format&fit=crop&w=600&q=80', 'sanduni.p@medicarehospital.lk', '0772223344'),
(103, 'Dr. Nishantha Silva', 'Chief Orthopedic Surgeon', 3, 'MBBS, MS (Orth), FRCS (Edin)', 4000.00, 'Room 312, Surgical Wing', 'Mon, Tue, Thu', '03:00 PM - 06:00 PM', 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=600&q=80', 'nishantha.s@medicarehospital.lk', '0773334455'),
(104, 'Dr. Chamari Wickramasinghe', 'Consultant Pediatrician', 4, 'MBBS, DCH, MD (Pediatrics)', 3000.00, 'Room 102, Children Care Center', 'Mon, Wed, Sat', '09:00 AM - 12:00 PM', 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=600&q=80', 'chamari.w@medicarehospital.lk', '0774445566'),
(105, 'Dr. Kithsiri Fernando', 'Consultant Oncologist', 5, 'MBBS, MD (Oncology), FRCRO', 4200.00, 'Room 405, Cancer Center', 'Wed, Fri, Sun', '02:00 PM - 05:00 PM', 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=600&q=80', 'kithsiri.f@medicarehospital.lk', '0775556677'),
(106, 'Dr. Priyantha Gunawardena', 'Consultant Gastroenterologist', 6, 'MBBS, MD, Fellowship in Endoscopy', 3600.00, 'Room 215, OPD Block', 'Tue, Fri', '04:30 PM - 07:30 PM', 'https://images.unsplash.com/photo-1582750433449-648ed127bb54?auto=format&fit=crop&w=600&q=80', 'priyantha.g@medicarehospital.lk', '0776667788')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 3. Insert Wards
INSERT INTO wards (id, name, total_beds, occupied_beds, available_beds, ward_type) VALUES
(1, 'ICU (Intensive Care)', 20, 16, 4, 'Critical'),
(2, 'CCU (Cardiac Care)', 15, 12, 3, 'Critical'),
(3, 'Female Surgical Ward', 30, 22, 8, 'General'),
(4, 'Male Surgical Ward', 30, 25, 5, 'General'),
(5, 'Pediatric Care Ward', 25, 18, 7, 'Specialized'),
(6, 'Private Luxury Suites', 12, 10, 2, 'Private')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 4. Insert Operation Theatres (OT)
INSERT INTO operation_theatres (id, suite_name, lead_surgeon, current_procedure, status, scheduled_time) VALUES
('OT-1', 'General Surgery Suite 1', 'Dr. Nishantha Silva', 'Laparoscopic Cholecystectomy', 'IN PROGRESS', '08:00 AM - 11:30 AM'),
('OT-2', 'Cardiac OT Suite 2', 'Dr. Anura Jayasinghe', 'Coronary Artery Bypass (CABG)', 'SCHEDULED', '12:00 PM - 04:00 PM'),
('OT-3', 'Neuro OT Suite 3', 'Dr. Sanduni Perera', 'Micro-Discectomy', 'READY', '04:30 PM - 07:00 PM')
ON DUPLICATE KEY UPDATE suite_name=VALUES(suite_name);

-- 5. Insert Pharmacy Inventory
INSERT INTO pharmacy_medicines (medicine_code, name, category, stock_quantity, unit_price) VALUES
('MED-01', 'Paracetamol 500mg', 'Analgesic', 1250, 15.00),
('MED-02', 'Amoxicillin 500mg', 'Antibiotic', 450, 45.00),
('MED-03', 'Atorvastatin 20mg', 'Cardiovascular', 320, 65.00),
('MED-04', 'Metformin 500mg', 'Diabetic Care', 800, 25.00),
('MED-05', 'Omeprazole 20mg', 'Gastroenterology', 600, 35.00),
('MED-06', 'Insulin Glargine Pen', 'Diabetic Care', 85, 3200.00)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 6. Insert Blood Bank Reserve Units
INSERT INTO blood_bank_inventory (blood_group, available_units, status) VALUES
('A+', 28, 'AVAILABLE'),
('A-', 8, 'LOW STOCK'),
('B+', 35, 'AVAILABLE'),
('B-', 12, 'AVAILABLE'),
('AB+', 18, 'AVAILABLE'),
('AB-', 5, 'CRITICAL'),
('O+', 42, 'AVAILABLE'),
('O-', 14, 'UNIVERSAL DONOR')
ON DUPLICATE KEY UPDATE available_units=VALUES(available_units);

-- ==========================================================================
-- PHARMACY MODULE SAMPLE DATA (9 Tables)
-- ==========================================================================

-- 1. Insert Medicine Categories
INSERT INTO medicine_categories (category_id, category_name, description, status) VALUES
(1, 'Painkillers & Analgesics', 'Pain relief, fever reducer and anti-inflammatory drugs', 'ACTIVE'),
(2, 'Antibiotics', 'Bacterial infection treatment and antimicrobial agents', 'ACTIVE'),
(3, 'Antihistamines', 'Allergy relief, anti-itch and cold remedies', 'ACTIVE'),
(4, 'Cardiovascular & Hypertension', 'Blood pressure management, cholesterol & heart care', 'ACTIVE'),
(5, 'Diabetes & Endocrine Care', 'Insulin, blood glucose regulators and oral hypoglycemics', 'ACTIVE'),
(6, 'Gastrointestinal & Ulcer', 'Antacids, proton pump inhibitors and digestive aids', 'ACTIVE'),
(7, 'Emergency & Critical Care', 'Intravenous fluids, emergency resuscitation and ICU meds', 'ACTIVE')
ON DUPLICATE KEY UPDATE category_name=VALUES(category_name);

-- 2. Insert Suppliers
INSERT INTO suppliers (supplier_id, supplier_name, contact_person, phone, email, address, status) VALUES
(1, 'State Pharmaceuticals Corporation (SPC)', 'Mr. Gamini Silva', '0112320351', 'orders@spc.lk', 'No. 75, Sir Baron Jayathilaka Mawatha, Colombo 01', 'ACTIVE'),
(2, 'A. Baur & Co. (Pvt) Ltd', 'Mrs. Dilhani Perera', '0112448061', 'pharma@baurs.com', 'Grandpass Road, Colombo 14', 'ACTIVE'),
(3, 'Hemas Pharmaceuticals', 'Mr. Rohan De Silva', '0114731731', 'info@hemaspharma.com', 'Hemas House, Braybrooke Place, Colombo 02', 'ACTIVE'),
(4, 'Sunshine Healthcare Lanka', 'Ms. Nimali Wickramasinghe', '0114702500', 'contact@sunshinehealth.lk', 'No. 45, Hyde Park Corner, Colombo 02', 'ACTIVE')
ON DUPLICATE KEY UPDATE supplier_name=VALUES(supplier_name);

-- 3. Insert Medicines
INSERT INTO medicines (medicine_id, medicine_name, generic_name, category_id, supplier_id, description, dosage_form, strength, unit, selling_price, reorder_level, status) VALUES
(1, 'Paracetamol 500mg (Panadol)', 'Paracetamol / Acetaminophen', 1, 1, 'Analgesic and antipyretic for fever and mild to moderate pain relief', 'Tablet', '500mg', 'Tablet', 15.00, 100, 'ACTIVE'),
(2, 'Amoxicillin 500mg Capsule', 'Amoxicillin Trihydrate', 2, 2, 'Broad-spectrum penicillin antibiotic for bacterial respiratory infections', 'Capsule', '500mg', 'Capsule', 45.00, 50, 'ACTIVE'),
(3, 'Ibuprofen 400mg Tablet', 'Ibuprofen', 1, 3, 'Nonsteroidal anti-inflammatory drug (NSAID) for severe joint & muscle pain', 'Tablet', '400mg', 'Tablet', 25.00, 40, 'ACTIVE'),
(4, 'Cetirizine 10mg Tablet', 'Cetirizine Dihydrochloride', 3, 4, 'Second-generation antihistamine for seasonal allergy & rhinitis relief', 'Tablet', '10mg', 'Tablet', 20.00, 30, 'ACTIVE'),
(5, 'Omeprazole 20mg Capsule', 'Omeprazole', 6, 2, 'Proton pump inhibitor (PPI) for gastric ulcer and acid reflux treatment', 'Capsule', '20mg', 'Capsule', 35.00, 50, 'ACTIVE'),
(6, 'Metformin 500mg Tablet', 'Metformin Hydrochloride', 5, 1, 'First-line medication for type 2 diabetes blood glucose control', 'Tablet', '500mg', 'Tablet', 22.00, 80, 'ACTIVE'),
(7, 'Salbutamol Inhaler 100mcg', 'Salbutamol / Albuterol', 7, 3, 'Fast-acting bronchodilator for quick asthma and bronchospasm relief', 'Drops', '100mcg', 'Inhaler', 850.00, 15, 'ACTIVE'),
(8, 'Atorvastatin 20mg Tablet', 'Atorvastatin Calcium', 4, 4, 'Statin medication to lower blood cholesterol and protect cardiac risk', 'Tablet', '20mg', 'Tablet', 65.00, 45, 'ACTIVE')
ON DUPLICATE KEY UPDATE medicine_name=VALUES(medicine_name);

-- 4. Insert Medicine Stock Batches
INSERT INTO medicine_stock (stock_id, medicine_id, batch_number, quantity, unit_cost, expiry_date, received_date, supplier_id, status) VALUES
(1, 1, 'BATCH-P001', 500, 10.00, '2027-06-30', '2026-01-15', 1, 'ACTIVE'),
(2, 1, 'BATCH-P002', 300, 10.50, '2027-12-15', '2026-03-10', 1, 'ACTIVE'),
(3, 2, 'BATCH-A500', 250, 32.00, '2027-04-20', '2026-02-01', 2, 'ACTIVE'),
(4, 3, 'BATCH-IB40', 15, 18.00, '2027-08-10', '2026-01-20', 3, 'ACTIVE'), -- Low Stock
(5, 4, 'BATCH-CT10', 400, 12.00, '2026-10-15', '2026-02-12', 4, 'ACTIVE'), -- Expiring within 30 days
(6, 5, 'BATCH-OM20', 0, 24.00, '2026-08-01', '2025-11-05', 2, 'EXPIRED')  -- Expired
ON DUPLICATE KEY UPDATE batch_number=VALUES(batch_number);

-- 5. Insert Stock Transactions History
INSERT INTO stock_transactions (transaction_id, medicine_id, stock_id, transaction_type, quantity, reference_id, transaction_date, performed_by, notes) VALUES
(1, 1, 1, 'STOCK_IN', 500, 'PO-2026-001', '2026-01-15 09:30:00', 'Admin Pharmacist', 'Initial stock purchase from SPC'),
(2, 2, 3, 'STOCK_IN', 250, 'PO-2026-002', '2026-02-01 11:00:00', 'Admin Pharmacist', 'Received batch A500 from Baurs'),
(3, 1, 1, 'DISPENSE', -20, 'DISP-1001', '2026-09-10 14:20:00', 'Dispensing Pharmacist', 'Dispensed for Prescription #1001')
ON DUPLICATE KEY UPDATE transaction_id=VALUES(transaction_id);

-- 6. Insert Prescriptions
INSERT INTO prescriptions (prescription_id, patient_id, patient_name, doctor_id, doctor_name, prescription_date, diagnosis, notes, status) VALUES
(1001, 1, 'Kasun Perera', 101, 'Dr. Anura Jayasinghe', '2026-09-14', 'Acute Fever and Mild Chest Congestion', 'Take medicines after meals with plenty of water', 'PENDING'),
(1002, 2, 'Nimali Fernando', 102, 'Dr. Sanduni Perera', '2026-09-15', 'Migraine & Tension Headache', 'Follow up in 2 weeks if symptoms persist', 'DISPENSED')
ON DUPLICATE KEY UPDATE prescription_id=VALUES(prescription_id);

-- 7. Insert Prescription Items
INSERT INTO prescription_items (prescription_item_id, prescription_id, medicine_id, dosage, frequency, duration, quantity_prescribed, instructions) VALUES
(1, 1001, 1, '500mg', '3 times / day', '5 days', 15, 'Take 1 tablet after meals'),
(2, 1001, 2, '500mg', '2 times / day', '7 days', 14, 'Take 1 capsule every 12 hours'),
(3, 1002, 3, '400mg', 'As needed', '3 days', 6, 'Take when headache starts')
ON DUPLICATE KEY UPDATE prescription_item_id=VALUES(prescription_item_id);

-- 8. Insert Dispensings
INSERT INTO dispensings (dispensing_id, prescription_id, patient_name, pharmacist_name, dispensing_date, total_amount, status, notes) VALUES
(1, 1002, 'Nimali Fernando', 'Head Pharmacist', '2026-09-15 10:15:00', 150.00, 'DISPENSED', 'Fully dispensed to patient at OPD Pharmacy counter')
ON DUPLICATE KEY UPDATE dispensing_id=VALUES(dispensing_id);

-- 9. Insert Dispensing Items
INSERT INTO dispensing_items (dispensing_item_id, dispensing_id, medicine_id, stock_id, quantity_dispensed, unit_price, subtotal) VALUES
(1, 1, 3, 4, 6, 25.00, 150.00)
ON DUPLICATE KEY UPDATE dispensing_item_id=VALUES(dispensing_item_id);

