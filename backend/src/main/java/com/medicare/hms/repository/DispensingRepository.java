package com.medicare.hms.repository;

import com.medicare.hms.model.Dispensing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DispensingRepository extends JpaRepository<Dispensing, Long> {
    List<Dispensing> findByDispensingDateAfter(LocalDateTime date);
    List<Dispensing> findAllByOrderByDispensingIdDesc();
}
