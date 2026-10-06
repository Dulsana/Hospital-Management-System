package com.medicare.hms.repository;

import com.medicare.hms.model.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    Optional<BloodUnit> findTopByOrderByIdDesc();
    Optional<BloodUnit> findByUnitNumber(String unitNumber);
    List<BloodUnit> findByBloodType(String bloodType);
    List<BloodUnit> findByStatus(String status);
    List<BloodUnit> findByBloodTypeAndStatus(String bloodType, String status);
    List<BloodUnit> findByExpiryDateBeforeAndStatusNot(LocalDateTime expiryDate, String status);
    List<BloodUnit> findByExpiryDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
}
