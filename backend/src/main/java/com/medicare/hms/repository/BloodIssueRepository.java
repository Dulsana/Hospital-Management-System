package com.medicare.hms.repository;

import com.medicare.hms.model.BloodIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodIssueRepository extends JpaRepository<BloodIssue, Long> {
    Optional<BloodIssue> findTopByOrderByIdDesc();
    Optional<BloodIssue> findByIssueNumber(String issueNumber);
    List<BloodIssue> findByRequestId(Long requestId);
}
