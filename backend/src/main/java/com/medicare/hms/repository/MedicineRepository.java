package com.medicare.hms.repository;

import com.medicare.hms.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByMedicineNameIgnoreCase(String medicineName);

    boolean existsByMedicineNameIgnoreCase(String medicineName);

    boolean existsByMedicineNameIgnoreCaseAndMedicineIdNot(String medicineName, Long medicineId);

    boolean existsByCategory_CategoryId(Long categoryId);

    boolean existsBySupplier_SupplierId(Long supplierId);

    List<Medicine> findByStatus(String status);

    @Query("SELECT m FROM Medicine m WHERE " +
           "(COALESCE(m.quantity, 0) <= COALESCE(m.reorderLevel, 10)) AND " +
           "(m.status IS NULL OR m.status != 'INACTIVE')")
    List<Medicine> findLowStockMedicines();

    @Query("SELECT m FROM Medicine m WHERE " +
           "m.expiryDate IS NOT NULL AND m.expiryDate < :currentDate AND " +
           "(m.status IS NULL OR m.status != 'INACTIVE')")
    List<Medicine> findExpiredMedicines(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT m FROM Medicine m WHERE " +
           "m.expiryDate IS NOT NULL AND m.expiryDate >= :fromDate AND m.expiryDate <= :toDate AND " +
           "(m.status IS NULL OR m.status != 'INACTIVE')")
    List<Medicine> findNearExpiryMedicines(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT m FROM Medicine m WHERE " +
           "(:query IS NULL OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " (m.genericName IS NOT NULL AND LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           " (m.categoryName IS NOT NULL AND LOWER(m.categoryName) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           " (m.manufacturer IS NOT NULL AND LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           " (m.supplierName IS NOT NULL AND LOWER(m.supplierName) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           " (m.batchNumber IS NOT NULL AND LOWER(m.batchNumber) LIKE LOWER(CONCAT('%', :query, '%')))) AND " +
           "(:name IS NULL OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:category IS NULL OR (m.categoryName IS NOT NULL AND LOWER(m.categoryName) LIKE LOWER(CONCAT('%', :category, '%')))) AND " +
           "(:manufacturer IS NULL OR (m.manufacturer IS NOT NULL AND LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :manufacturer, '%')))) AND " +
           "(:supplier IS NULL OR (m.supplierName IS NOT NULL AND LOWER(m.supplierName) LIKE LOWER(CONCAT('%', :supplier, '%')))) AND " +
           "(:batchNumber IS NULL OR (m.batchNumber IS NOT NULL AND LOWER(m.batchNumber) LIKE LOWER(CONCAT('%', :batchNumber, '%'))))")
    List<Medicine> searchMedicinesAdvanced(
            @Param("query") String query,
            @Param("name") String name,
            @Param("category") String category,
            @Param("manufacturer") String manufacturer,
            @Param("supplier") String supplier,
            @Param("batchNumber") String batchNumber
    );

    @Query("SELECT m FROM Medicine m WHERE " +
           "(:query IS NULL OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :query, '%')) OR (m.genericName IS NOT NULL AND LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')))) AND " +
           "(:categoryId IS NULL OR (m.category IS NOT NULL AND m.category.categoryId = :categoryId)) AND " +
           "(:supplierId IS NULL OR (m.supplier IS NOT NULL AND m.supplier.supplierId = :supplierId)) AND " +
           "(:status IS NULL OR m.status = :status)")
    List<Medicine> filterMedicines(@Param("query") String query,
                                  @Param("categoryId") Long categoryId,
                                  @Param("supplierId") Long supplierId,
                                  @Param("status") String status);
}
