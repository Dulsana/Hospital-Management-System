package com.medicare.hms.repository;

import com.medicare.hms.model.MedicineCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineCategoryRepository extends JpaRepository<MedicineCategory, Long> {
    Optional<MedicineCategory> findByCategoryNameIgnoreCase(String categoryName);
    List<MedicineCategory> findByStatus(String status);
}
