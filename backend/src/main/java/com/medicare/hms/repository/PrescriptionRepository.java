package com.medicare.hms.repository;

import com.medicare.hms.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByStatus(String status);
    List<Prescription> findByAppointmentId(Long appointmentId);
    Optional<Prescription> findFirstByAppointmentId(Long appointmentId);
    List<Prescription> findByPatientNameContainingIgnoreCaseOrDoctorNameContainingIgnoreCase(String patientName, String doctorName);
}
