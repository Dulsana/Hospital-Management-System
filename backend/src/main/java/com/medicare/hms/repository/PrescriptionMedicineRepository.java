package com.medicare.hms.repository;

import com.medicare.hms.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionItem, Long> {
    List<PrescriptionItem> findByPrescription_PrescriptionId(Long prescriptionId);
    Optional<PrescriptionItem> findByPrescription_PrescriptionIdAndMedicine_MedicineId(Long prescriptionId, Long medicineId);
    boolean existsByMedicine_MedicineId(Long medicineId);
}
