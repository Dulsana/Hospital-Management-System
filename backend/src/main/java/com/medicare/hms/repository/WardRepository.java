package com.medicare.hms.repository;

import com.medicare.hms.model.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    boolean existsByWardNumber(Integer wardNumber);
    boolean existsByWardNumberAndWardIdNot(Integer wardNumber, String wardId);
}
