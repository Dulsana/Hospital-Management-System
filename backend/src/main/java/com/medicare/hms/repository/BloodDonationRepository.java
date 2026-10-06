package com.medicare.hms.repository;

import com.medicare.hms.model.BloodDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodDonationRepository extends JpaRepository<BloodDonation, Long> {
    Optional<BloodDonation> findTopByOrderByIdDesc();
    Optional<BloodDonation> findByDonationNumber(String donationNumber);
    List<BloodDonation> findByDonorId(Long donorId);
    List<BloodDonation> findByBloodType(String bloodType);
}
