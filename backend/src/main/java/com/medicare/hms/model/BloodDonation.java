package com.medicare.hms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_donations")
public class BloodDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donation_number", nullable = false, unique = true, length = 30)
    private String donationNumber;

    @Column(name = "donor_id", nullable = false)
    private Long donorId;

    @Column(name = "donation_date", nullable = false)
    private LocalDateTime donationDate = LocalDateTime.now();

    @Column(name = "blood_type", nullable = false, length = 10)
    private String bloodType;

    @Column(name = "volume_ml", nullable = false)
    private Integer volumeMl = 450;

    @Column(name = "donation_location", length = 100)
    private String donationLocation = "Main Blood Center";

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "eligibility_status", length = 30)
    private String eligibilityStatus = "ELIGIBLE";

    @Column(name = "screening_notes", columnDefinition = "TEXT")
    private String screeningNotes;

    @Column(name = "status", length = 30)
    private String status = "REGISTERED";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public BloodDonation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDonationNumber() { return donationNumber; }
    public void setDonationNumber(String donationNumber) { this.donationNumber = donationNumber; }

    public Long getDonorId() { return donorId; }
    public void setDonorId(Long donorId) { this.donorId = donorId; }

    public LocalDateTime getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDateTime donationDate) { this.donationDate = donationDate; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public Integer getVolumeMl() { return volumeMl; }
    public void setVolumeMl(Integer volumeMl) { this.volumeMl = volumeMl; }

    public String getDonationLocation() { return donationLocation; }
    public void setDonationLocation(String donationLocation) { this.donationLocation = donationLocation; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }

    public String getScreeningNotes() { return screeningNotes; }
    public void setScreeningNotes(String screeningNotes) { this.screeningNotes = screeningNotes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
