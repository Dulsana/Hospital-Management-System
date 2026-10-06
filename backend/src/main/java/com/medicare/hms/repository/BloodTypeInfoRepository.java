package com.medicare.hms.repository;

import com.medicare.hms.model.BloodTypeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloodTypeInfoRepository extends JpaRepository<BloodTypeInfo, Long> {
    Optional<BloodTypeInfo> findByBloodGroup(String bloodGroup);
}
