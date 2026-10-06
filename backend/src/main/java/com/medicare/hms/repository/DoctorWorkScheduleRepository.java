package com.medicare.hms.repository;

import com.medicare.hms.model.DoctorWorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DoctorWorkScheduleRepository extends JpaRepository<DoctorWorkSchedule, Long> {
    List<DoctorWorkSchedule> findByDoctorId(Long doctorId);
    List<DoctorWorkSchedule> findByDoctorIdAndScheduleDate(Long doctorId, LocalDate scheduleDate);
    List<DoctorWorkSchedule> findByScheduleDate(LocalDate scheduleDate);
}
