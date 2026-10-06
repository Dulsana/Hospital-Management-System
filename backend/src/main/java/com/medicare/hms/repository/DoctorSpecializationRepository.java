package com.medicare.hms.repository;

import com.medicare.hms.model.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Long> {
    Optional<DoctorSpecialization> findByName(String name);
}
