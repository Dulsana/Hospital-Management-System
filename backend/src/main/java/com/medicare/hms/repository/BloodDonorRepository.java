package com.medicare.hms.repository;

import com.medicare.hms.model.BloodDonor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonorRepository extends JpaRepository<BloodDonor, Long> {
    Optional<BloodDonor> findTopByOrderByIdDesc();
    Optional<BloodDonor> findByDonorNumber(String donorNumber);
    List<BloodDonor> findByBloodType(String bloodType);
    List<BloodDonor> findByStatus(String status);
}
