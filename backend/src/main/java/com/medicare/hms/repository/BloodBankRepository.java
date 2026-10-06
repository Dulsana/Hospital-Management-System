package com.medicare.hms.repository;

import com.medicare.hms.model.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
    Optional<BloodBank> findByBloodGroupIgnoreCase(String bloodGroup);
}
