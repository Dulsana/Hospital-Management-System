package com.medicare.hms.repository;

import com.medicare.hms.model.BloodTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodTestRepository extends JpaRepository<BloodTest, Long> {
    Optional<BloodTest> findByDonationId(Long donationId);
    Optional<BloodTest> findByBloodUnitId(Long bloodUnitId);
}
