package com.medicare.hms.repository;

import com.medicare.hms.model.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {
    List<DoctorLeave> findByDoctorId(Long doctorId);
    List<DoctorLeave> findByStatus(String status);
    List<DoctorLeave> findByDoctorIdAndStatus(Long doctorId, String status);
}
