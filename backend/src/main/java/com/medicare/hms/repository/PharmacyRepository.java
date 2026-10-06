package com.medicare.hms.repository;

import com.medicare.hms.model.PharmacyMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<PharmacyMedicine, Long> {
    List<PharmacyMedicine> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String nameKeyword, String categoryKeyword);
}
