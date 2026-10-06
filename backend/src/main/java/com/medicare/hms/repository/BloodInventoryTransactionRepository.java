package com.medicare.hms.repository;

import com.medicare.hms.model.BloodInventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodInventoryTransactionRepository extends JpaRepository<BloodInventoryTransaction, Long> {
    List<BloodInventoryTransaction> findByBloodUnitId(Long bloodUnitId);
    List<BloodInventoryTransaction> findByTransactionType(String transactionType);
}
