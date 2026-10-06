package com.medicare.hms.repository;

import com.medicare.hms.model.DispensingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispensingRecordRepository extends JpaRepository<DispensingRecord, Long> {
    List<DispensingRecord> findByPrescriptionId(Long prescriptionId);
    List<DispensingRecord> findAllByOrderByDispensingIdDesc();
}
