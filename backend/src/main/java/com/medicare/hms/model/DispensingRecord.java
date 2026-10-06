package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispensing_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispensing_id")
    private Long dispensingId;

    @Column(name = "prescription_id", nullable = false)
    private Long prescriptionId;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "pharmacist_id")
    private Long pharmacistId;

    @Column(name = "pharmacist_name")
    private String pharmacistName;

    @Column(name = "dispensing_date")
    private LocalDateTime dispensingDate;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column
    @Builder.Default
    private String status = "DISPENSED"; // DISPENSED, PARTIALLY_DISPENSED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (this.dispensingDate == null) this.dispensingDate = LocalDateTime.now();
        if (this.status == null) this.status = "DISPENSED";
    }

    public Long getDispensingId() { return dispensingId; }
    public void setDispensingId(Long dispensingId) { this.dispensingId = dispensingId; }
    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public Long getPharmacistId() { return pharmacistId; }
    public void setPharmacistId(Long pharmacistId) { this.pharmacistId = pharmacistId; }
    public String getPharmacistName() { return pharmacistName; }
    public void setPharmacistName(String pharmacistName) { this.pharmacistName = pharmacistName; }
    public LocalDateTime getDispensingDate() { return dispensingDate; }
    public void setDispensingDate(LocalDateTime dispensingDate) { this.dispensingDate = dispensingDate; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
