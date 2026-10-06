package com.medicare.hms.repository;

import com.medicare.hms.model.MedicineStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineStockRepository extends JpaRepository<MedicineStock, Long> {

    List<MedicineStock> findByMedicine_MedicineIdAndStatus(Long medicineId, String status);

    boolean existsByMedicine_MedicineId(Long medicineId);

    boolean existsBySupplier_SupplierId(Long supplierId);

    @Query("SELECT SUM(s.quantity) FROM MedicineStock s WHERE s.medicine.medicineId = :medicineId AND s.status = 'ACTIVE' AND s.expiryDate >= CURRENT_DATE")
    Integer getTotalAvailableQuantityForMedicine(@Param("medicineId") Long medicineId);

    List<MedicineStock> findByExpiryDateBeforeAndStatus(LocalDate date, String status);

    List<MedicineStock> findByExpiryDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status);
}
