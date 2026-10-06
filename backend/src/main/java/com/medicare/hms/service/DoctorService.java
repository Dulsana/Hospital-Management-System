package com.medicare.hms.service;

import com.medicare.hms.util.PhoneValidator;
import com.medicare.hms.model.Department;
import com.medicare.hms.model.Doctor;
import com.medicare.hms.repository.AppointmentRepository;
import com.medicare.hms.repository.DepartmentRepository;
import com.medicare.hms.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         DepartmentRepository departmentRepository,
                         AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsByDepartment(String departmentName) {
        if (departmentName == null || departmentName.equalsIgnoreCase("all")) {
            return doctorRepository.findAll();
        }
        return doctorRepository.findByDepartmentNameIgnoreCase(departmentName);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        validate(doctor);
        doctor.setId(null);
        doctor.setDepartment(resolveDepartment(doctor.getDepartment()));
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, Doctor changes) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));
        validate(changes);

        existing.setName(changes.getName().trim());
        existing.setTitle(changes.getTitle().trim());
        existing.setDepartment(resolveDepartment(changes.getDepartment()));
        existing.setQualifications(changes.getQualifications());
        existing.setFee(changes.getFee());
        existing.setRoomNumber(changes.getRoomNumber());
        existing.setScheduleDays(changes.getScheduleDays());
        existing.setTimeSlot(changes.getTimeSlot());
        existing.setEmail(changes.getEmail());
        existing.setPhone(changes.getPhone());
        existing.setGender(changes.getGender());
        if (changes.getImageUrl() != null && !changes.getImageUrl().isBlank()) {
            existing.setImageUrl(changes.getImageUrl());
        }
        return doctorRepository.save(existing);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new IllegalArgumentException("Doctor not found with ID: " + id);
        }
        // Appointments keep a foreign key to the doctor, so a doctor with
        // bookings cannot be removed without losing patient history.
        long bookings = appointmentRepository.countByDoctor_Id(id);
        if (bookings > 0) {
            throw new IllegalStateException("This doctor has " + bookings
                    + " appointment(s). Delete or reassign those appointments first.");
        }
        doctorRepository.deleteById(id);
    }

    private void validate(Doctor doctor) {
        if (doctor.getName() == null || doctor.getName().isBlank()) {
            throw new IllegalArgumentException("Doctor name is required.");
        }
        if (doctor.getTitle() == null || doctor.getTitle().isBlank()) {
            throw new IllegalArgumentException("Designation title is required.");
        }
        if (doctor.getFee() == null || doctor.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Consultation fee must be 0 or more.");
        }
        // Phone is optional for doctors, but if one is typed it must be a valid Sri Lankan number
        doctor.setPhone(PhoneValidator.optionalValid(doctor.getPhone(), "Doctor phone number"));
    }

    // The page sends only the department id (or name); load the real row so the
    // response contains the full department details.
    private Department resolveDepartment(Department dept) {
        if (dept == null) {
            return null;
        }
        if (dept.getId() != null) {
            return departmentRepository.findById(dept.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + dept.getId()));
        }
        if (dept.getName() != null && !dept.getName().isBlank()) {
            return departmentRepository.findByNameIgnoreCase(dept.getName().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dept.getName()));
        }
        return null;
    }
}
