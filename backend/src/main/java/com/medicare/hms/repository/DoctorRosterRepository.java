package com.medicare.hms.repository;

import com.medicare.hms.model.DoctorRoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorRosterRepository extends JpaRepository<DoctorRoster, Long> {
    List<DoctorRoster> findByDoctorId(Long doctorId);
    List<DoctorRoster> findByDoctorIdAndRosterDate(Long doctorId, LocalDate rosterDate);
    List<DoctorRoster> findByRosterDate(LocalDate rosterDate);
}
