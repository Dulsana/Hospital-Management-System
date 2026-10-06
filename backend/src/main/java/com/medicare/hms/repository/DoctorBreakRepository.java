package com.medicare.hms.repository;

import com.medicare.hms.model.DoctorBreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorBreakRepository extends JpaRepository<DoctorBreak, Long> {
    List<DoctorBreak> findByDoctorId(Long doctorId);
    List<DoctorBreak> findByDoctorIdAndBreakDate(Long doctorId, LocalDate breakDate);
}
