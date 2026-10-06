package com.medicare.hms.repository;

import com.medicare.hms.model.ConsultationSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultationSlotRepository extends JpaRepository<ConsultationSlot, Long> {
    List<ConsultationSlot> findByDoctorId(Long doctorId);
    List<ConsultationSlot> findByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);
    List<ConsultationSlot> findByDoctorIdAndSlotDateAndStatus(Long doctorId, LocalDate slotDate, String status);
    List<ConsultationSlot> findByScheduleId(Long scheduleId);
}
