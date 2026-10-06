package com.medicare.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_tests")
public class BloodTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donation_id", nullable = false)
    private Long donationId;

    @Column(name = "blood_unit_id", nullable = false)
    private Long bloodUnitId;

    @Column(name = "blood_type_result", nullable = false, length = 10)
    private String bloodTypeResult;

    @Column(name = "hiv_result", length = 20)
    private String hivResult = "NEGATIVE";

    @Column(name = "hepatitis_b_result", length = 20)
    private String hepatitisBResult = "NEGATIVE";

    @Column(name = "hepatitis_c_result", length = 20)
    private String hepatitisCResult = "NEGATIVE";

    @Column(name = "syphilis_result", length = 20)
    private String syphilisResult = "NEGATIVE";

    @Column(name = "malaria_result", length = 20)
    private String malariaResult = "NEGATIVE";

    @Column(name = "other_test_result", length = 50)
    private String otherTestResult = "NEGATIVE";

    @Column(name = "tested_by", length = 100)
    private String testedBy = "Lab Specialist";

    @Column(name = "test_date")
    private LocalDateTime testDate = LocalDateTime.now();

    @Column(name = "overall_result", length = 20)
    private String overallResult = "PENDING";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public BloodTest() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDonationId() { return donationId; }
    public void setDonationId(Long donationId) { this.donationId = donationId; }

    public Long getBloodUnitId() { return bloodUnitId; }
    public void setBloodUnitId(Long bloodUnitId) { this.bloodUnitId = bloodUnitId; }

    public String getBloodTypeResult() { return bloodTypeResult; }
    public void setBloodTypeResult(String bloodTypeResult) { this.bloodTypeResult = bloodTypeResult; }

    public String getHivResult() { return hivResult; }
    public void setHivResult(String hivResult) { this.hivResult = hivResult; }

    public String getHepatitisBResult() { return hepatitisBResult; }
    public void setHepatitisBResult(String hepatitisBResult) { this.hepatitisBResult = hepatitisBResult; }

    public String getHepatitisCResult() { return hepatitisCResult; }
    public void setHepatitisCResult(String hepatitisCResult) { this.hepatitisCResult = hepatitisCResult; }

    public String getSyphilisResult() { return syphilisResult; }
    public void setSyphilisResult(String syphilisResult) { this.syphilisResult = syphilisResult; }

    public String getMalariaResult() { return malariaResult; }
    public void setMalariaResult(String malariaResult) { this.malariaResult = malariaResult; }

    public String getOtherTestResult() { return otherTestResult; }
    public void setOtherTestResult(String otherTestResult) { this.otherTestResult = otherTestResult; }

    public String getTestedBy() { return testedBy; }
    public void setTestedBy(String testedBy) { this.testedBy = testedBy; }

    public LocalDateTime getTestDate() { return testDate; }
    public void setTestDate(LocalDateTime testDate) { this.testDate = testDate; }

    public String getOverallResult() { return overallResult; }
    public void setOverallResult(String overallResult) { this.overallResult = overallResult; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
