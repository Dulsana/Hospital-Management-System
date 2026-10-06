package com.medicare.hms.service;

import com.medicare.hms.util.PhoneValidator;
import com.medicare.hms.model.Appointment;
import com.medicare.hms.model.Doctor;
import com.medicare.hms.repository.AppointmentRepository;
import com.medicare.hms.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final Set<String> STATUSES = Set.of("CONFIRMED", "PENDING", "COMPLETED", "CANCELLED");

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // A patient's own bookings (matched on the email they log in with)
    public List<Appointment> getAppointmentsByPatientEmail(String email) {
        return appointmentRepository.findByPatientEmailIgnoreCase(email.trim());
    }

    public Optional<Appointment> getAppointmentByReference(String referenceCode) {
        return appointmentRepository.findByReferenceCode(referenceCode);
    }

    @Transactional
    public Appointment createAppointment(Long doctorId, String patientName, String patientPhone,
                                        String patientEmail, LocalDate appointmentDate, String timeSlot, String customRefCode) {
        validatePatient(patientName, patientPhone);
        Doctor doctor = findDoctor(doctorId);

        String refCode = (customRefCode != null && !customRefCode.isBlank())
                ? customRefCode
                : "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        if (appointmentRepository.findByReferenceCode(refCode).isPresent()) {
            refCode = "MED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        Appointment appointment = Appointment.builder()
                .referenceCode(refCode)
                .doctor(doctor)
                .patientName(patientName.trim())
                .patientPhone(PhoneValidator.normalize(patientPhone))
                .patientEmail(patientEmail)
                .appointmentDate(appointmentDate)
                .timeSlot(timeSlot)
                .status("CONFIRMED")
                .build();

        return appointmentRepository.save(appointment);
    }

    // Edit / reschedule an existing appointment
    @Transactional
    public Appointment updateAppointment(Long id, Long doctorId, String patientName, String patientPhone,
                                         String patientEmail, LocalDate appointmentDate, String timeSlot) {
        Appointment appointment = findAppointment(id);
        if ("CANCELLED".equals(appointment.getStatus())) {
            throw new IllegalStateException("A cancelled appointment cannot be changed. Please book a new one.");
        }
        validatePatient(patientName, patientPhone);

        if (doctorId != null) appointment.setDoctor(findDoctor(doctorId));
        appointment.setPatientName(patientName.trim());
        appointment.setPatientPhone(PhoneValidator.normalize(patientPhone));
        appointment.setPatientEmail(patientEmail);
        if (appointmentDate != null) appointment.setAppointmentDate(appointmentDate);
        if (timeSlot != null && !timeSlot.isBlank()) appointment.setTimeSlot(timeSlot);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment updateStatus(Long id, String status) {
        String newStatus = status == null ? "" : status.trim().toUpperCase();
        if (!STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Status must be one of " + STATUSES);
        }
        Appointment appointment = findAppointment(id);
        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.delete(findAppointment(id));
    }

    private Appointment findAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));
    }

    private Doctor findDoctor(Long doctorId) {
        if (doctorId == null) {
            throw new IllegalArgumentException("Please select a doctor.");
        }
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
    }

    private void validatePatient(String patientName, String patientPhone) {
        if (patientName == null || patientName.isBlank()) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        PhoneValidator.requireValid(patientPhone, "Patient phone number");
    }
}
