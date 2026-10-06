package com.medicare.hms.repository;

import com.medicare.hms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByReferenceCode(String referenceCode);
    long countByDoctor_Id(Long doctorId);
    List<Appointment> findByPatientEmailIgnoreCase(String patientEmail);
}
