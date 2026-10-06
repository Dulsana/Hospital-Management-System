package com.medicare.hms.repository;

import com.medicare.hms.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByMedicine_MedicineIdOrderByTransactionIdDesc(Long medicineId);
    List<StockTransaction> findAllByOrderByTransactionIdDesc();
    List<StockTransaction> findByStock_StockId(Long stockId);
}
