package com.medicare.hms.repository;

import com.medicare.hms.model.OperationTheatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTheatreRepository extends JpaRepository<OperationTheatre, String> {
}
