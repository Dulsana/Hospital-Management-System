package com.medicare.hms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispensingResponse {
    private Long dispensingId;
    private Long prescriptionId;
    private String patientName;
    private Long pharmacistId;
    private String pharmacistName;
    private LocalDateTime dispensingDate;
    private Double totalAmount;
    private String status;
    private String notes;
    private List<PrescriptionMedicineResponse> items;

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
    public List<PrescriptionMedicineResponse> getItems() { return items; }
    public void setItems(List<PrescriptionMedicineResponse> items) { this.items = items; }
}
