package com.medicare.hms.repository;

import com.medicare.hms.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByDepartmentNameIgnoreCase(String departmentName);
    List<Doctor> findByNameContainingIgnoreCase(String keyword);
}
