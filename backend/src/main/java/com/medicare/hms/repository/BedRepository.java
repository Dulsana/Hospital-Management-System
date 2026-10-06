package com.medicare.hms.repository;

import com.medicare.hms.model.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BedRepository extends JpaRepository<Bed, String> {
    List<Bed> findByWardId(String wardId);
    long countByWardId(String wardId);
    boolean existsByWardIdAndBedNumber(String wardId, Integer bedNumber);
    boolean existsByWardIdAndBedNumberAndBedIdNot(String wardId, Integer bedNumber, String bedId);
}
