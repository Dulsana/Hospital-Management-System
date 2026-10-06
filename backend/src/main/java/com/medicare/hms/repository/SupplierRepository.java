package com.medicare.hms.repository;

import com.medicare.hms.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findBySupplierNameIgnoreCase(String supplierName);
    List<Supplier> findByStatus(String status);
}
