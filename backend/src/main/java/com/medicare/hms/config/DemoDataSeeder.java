package com.medicare.hms.config;

import com.medicare.hms.model.Bed;
import com.medicare.hms.model.MedicineCategory;
import com.medicare.hms.model.Supplier;
import com.medicare.hms.model.Ward;
import com.medicare.hms.repository.BedRepository;
import com.medicare.hms.repository.DepartmentRepository;
import com.medicare.hms.repository.DoctorRepository;
import com.medicare.hms.repository.MedicineCategoryRepository;
import com.medicare.hms.repository.SupplierRepository;
import com.medicare.hms.repository.WardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Fills an EMPTY database with the starter data the pages need
 * (departments, doctors, pharmacy categories and suppliers).
 * Each table is only seeded when it has no rows, so existing data is never touched.
 *
 * Doctors and departments are inserted with fixed IDs (1-8 and 101-108) because
 * they match database/data.sql and the home page booking form.
 */
@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbc;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final WardRepository wardRepository;
    private final BedRepository bedRepository;

    public DemoDataSeeder(JdbcTemplate jdbc,
                          DepartmentRepository departmentRepository,
                          DoctorRepository doctorRepository,
                          MedicineCategoryRepository categoryRepository,
                          SupplierRepository supplierRepository,
                          WardRepository wardRepository,
                          BedRepository bedRepository) {
        this.jdbc = jdbc;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
    }

    @Override
    public void run(String... args) {
        seedDepartments();
        seedDoctors();
        seedPharmacyLookups();
        seedWardsAndBeds();
    }

    private void seedWardsAndBeds() {
        if (wardRepository.count() > 0) return;
        Object[][] wards = {
                {"WD-ICU", 1, "ICU (Intensive Care)", 6},
                {"WD-CCU", 2, "CCU (Cardiac Care)", 4},
                {"WD-GEN", 3, "General Ward", 8},
                {"WD-PED", 4, "Pediatric Ward", 5}
        };
        String[] pattern = {"Occupied", "Available", "Occupied", "Reserved", "Available", "Maintenance", "Available", "Occupied"};
        for (Object[] w : wards) {
            String wardId = (String) w[0];
            wardRepository.save(Ward.builder().wardId(wardId).wardNumber((Integer) w[1]).wardType((String) w[2]).build());
            int beds = (Integer) w[3];
            for (int i = 1; i <= beds; i++) {
                String status = pattern[(i - 1) % pattern.length];
                bedRepository.save(Bed.builder()
                        .bedId(String.format("%s-B%02d", wardId.substring(3), i))
                        .bedNumber(i)
                        .status(status)
                        .wardId(wardId)
                        .patientName("Occupied".equals(status) ? "Patient " + wardId.substring(3) + "-" + i : null)
                        .build());
            }
        }
    }

    private void seedDepartments() {
        if (departmentRepository.count() > 0) return;
        Object[][] rows = {
                {1, "Cardiology", "Heart, coronary & vascular disease treatment unit", "fa-heart-pulse", "Dr. Anura Jayasinghe"},
                {2, "Neurology", "Brain, spinal cord & nerve disorders management", "fa-brain", "Dr. Sanduni Perera"},
                {3, "Orthopedics", "Bone, joint replacement & musculoskeletal surgery", "fa-bone", "Dr. Nishantha Silva"},
                {4, "Pediatrics", "Child healthcare, vaccination & neonatal care", "fa-baby", "Dr. Chamari Wickramasinghe"},
                {5, "Oncology", "Cancer diagnosis, chemotherapy & radiotherapy", "fa-ribbon", "Dr. Kithsiri Fernando"},
                {6, "Radiology", "X-ray, CT, MRI & ultrasound imaging", "fa-x-ray", "Dr. Nimali Rathnayake"},
                {7, "Emergency Care", "24/7 accident & emergency treatment unit", "fa-truck-medical", "Dr. Roshan Amaratunga"},
                {8, "Gastroenterology", "Digestive system, liver & endoscopy services", "fa-stethoscope", "Dr. Priyantha Gunawardena"}
        };
        for (Object[] r : rows) {
            jdbc.update("INSERT INTO departments (id, name, description, icon_code, head_of_department) VALUES (?, ?, ?, ?, ?)", r);
        }
    }

    private void seedDoctors() {
        if (doctorRepository.count() > 0) return;
        Object[][] rows = {
                {101, "Dr. Anura Jayasinghe", "Senior Consultant Cardiologist", "Cardiology", "MBBS, MD (Cardiology), FRCP (UK)", 3500, "Room 204, Main Building", "Mon, Wed, Fri", "04:00 PM - 07:00 PM", "photo-1622253692010-333f2da6031d", "anura.j@medicarehospital.lk", "0771112233", "male"},
                {102, "Dr. Sanduni Perera", "Consultant Neurologist", "Neurology", "MBBS, MD (Neurology), MRCP (UK)", 3800, "Room 108, Specialist Wing", "Tue, Thu, Sat", "05:00 PM - 08:00 PM", "photo-1594824813566-78a9956461a5", "sanduni.p@medicarehospital.lk", "0772223344", "female"},
                {103, "Dr. Nishantha Silva", "Chief Orthopedic Surgeon", "Orthopedics", "MBBS, MS (Orth), FRCS (Edin)", 4000, "Room 312, Surgical Wing", "Mon, Tue, Thu", "03:00 PM - 06:00 PM", "photo-1537368910025-700350fe46c7", "nishantha.s@medicarehospital.lk", "0773334455", "male"},
                {104, "Dr. Chamari Wickramasinghe", "Consultant Pediatrician", "Pediatrics", "MBBS, DCH, MD (Pediatrics)", 3000, "Room 102, Children Care Center", "Mon, Wed, Sat", "09:00 AM - 12:00 PM", "photo-1559839734-2b71ea197ec2", "chamari.w@medicarehospital.lk", "0774445566", "female"},
                {105, "Dr. Kithsiri Fernando", "Consultant Oncologist", "Oncology", "MBBS, MD (Oncology), FRCRO", 4200, "Room 405, Cancer Center", "Wed, Fri, Sun", "02:00 PM - 05:00 PM", "photo-1612349317150-e413f6a5b16d", "kithsiri.f@medicarehospital.lk", "0775556677", "male"},
                {106, "Dr. Priyantha Gunawardena", "Consultant Gastroenterologist", "Gastroenterology", "MBBS, MD, Fellowship in Endoscopy", 3600, "Room 215, OPD Block", "Tue, Fri", "04:30 PM - 07:30 PM", "photo-1582750433449-648ed127bb54", "priyantha.g@medicarehospital.lk", "0776667788", "male"},
                {107, "Dr. Nimali Rathnayake", "Senior Radiologist", "Radiology", "MBBS, MD (Radiology), FRCR (UK)", 3200, "Room 501, Imaging Center", "Mon, Wed, Thu, Fri", "08:00 AM - 01:00 PM", "photo-1594824813566-78a9956461a5", "nimali.r@medicarehospital.lk", "0777778899", "female"},
                {108, "Dr. Roshan Amaratunga", "Emergency Medicine Specialist", "Emergency Care", "MBBS, MRCEM, PGDip Critical Care", 2500, "Emergency Block - ETU", "Mon, Tue, Wed, Thu, Fri, Sat, Sun", "24 Hours (On-Call)", "photo-1612349317150-e413f6a5b16d", "roshan.a@medicarehospital.lk", "0778889900", "male"}
        };
        for (Object[] r : rows) {
            Long departmentId = departmentRepository.findByNameIgnoreCase((String) r[3])
                    .map(d -> d.getId())
                    .orElse(null);
            String imageUrl = "https://images.unsplash.com/" + r[9] + "?auto=format&fit=crop&w=600&q=80";
            jdbc.update("INSERT INTO doctors (id, name, title, department_id, qualifications, fee, room_number, "
                            + "schedule_days, time_slot, image_url, email, phone, gender) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    r[0], r[1], r[2], departmentId, r[4], r[5], r[6], r[7], r[8], imageUrl, r[10], r[11], r[12]);
        }
    }

    private void seedPharmacyLookups() {
        if (categoryRepository.count() == 0) {
            String[][] categories = {
                    {"Painkillers & Analgesics", "Pain relief and fever reducing medicines"},
                    {"Antibiotics", "Medicines that treat bacterial infections"},
                    {"Antihistamines", "Allergy relief medicines"},
                    {"Cardiovascular & Hypertension", "Heart and blood pressure medicines"},
                    {"Diabetes & Endocrine Care", "Blood sugar and hormone medicines"},
                    {"Gastrointestinal & Ulcer", "Stomach, acidity and ulcer medicines"}
            };
            for (String[] c : categories) {
                MedicineCategory category = new MedicineCategory();
                category.setCategoryName(c[0]);
                category.setDescription(c[1]);
                category.setStatus("ACTIVE");
                categoryRepository.save(category);
            }
        }
        if (supplierRepository.count() == 0) {
            String[][] suppliers = {
                    {"State Pharmaceuticals Corporation (SPC)", "Procurement Desk", "0112345678", "orders@spc.lk"},
                    {"A. Baur & Co. (Pvt) Ltd", "Sales Office", "0112456789", "pharma@baurs.com"},
                    {"Hemas Pharmaceuticals", "Customer Care", "0112567890", "info@hemas.com"},
                    {"Sunshine Healthcare Lanka", "Distribution", "0112678901", "sales@sunshine.lk"}
            };
            for (String[] s : suppliers) {
                Supplier supplier = new Supplier();
                supplier.setSupplierName(s[0]);
                supplier.setContactPerson(s[1]);
                supplier.setPhone(s[2]);
                supplier.setEmail(s[3]);
                supplier.setStatus("ACTIVE");
                supplierRepository.save(supplier);
            }
        }
    }
}
