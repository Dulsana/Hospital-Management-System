package com.medicare.hms.repository;

import com.medicare.hms.model.BloodRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, Long> {
    Optional<BloodRequestDetail> findTopByOrderByIdDesc();
    Optional<BloodRequestDetail> findByRequestNumber(String requestNumber);
    List<BloodRequestDetail> findByStatus(String status);
    List<BloodRequestDetail> findByBloodType(String bloodType);
}
