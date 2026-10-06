package com.medicare.hms.service;

import com.medicare.hms.dto.*;

import java.util.List;

public interface PharmacyService {

    // Medicine Operations
    MedicineResponse createMedicine(MedicineRequest request);
    List<MedicineResponse> getAllMedicines();
    MedicineResponse getMedicineById(Long id);
    MedicineResponse updateMedicine(Long id, MedicineRequest request);
    void deleteMedicine(Long id);
    List<MedicineResponse> searchMedicines(String query, String name, String category, String manufacturer, String supplier, String batchNumber);
    List<MedicineResponse> getLowStockMedicines();
    List<MedicineResponse> getExpiredMedicines();
    List<MedicineResponse> getNearExpiryMedicines(Integer days);

    // Prescription Operations
    PrescriptionResponse createPrescription(PrescriptionRequest request);
    List<PrescriptionResponse> getAllPrescriptions();
    PrescriptionResponse getPrescriptionById(Long id);
    PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request);
    void deletePrescription(Long id);
    PrescriptionResponse getPrescriptionByAppointment(Long appointmentId);

    // Prescription Medicine Items
    PrescriptionResponse addMedicineToPrescription(Long prescriptionId, PrescriptionMedicineRequest request);
    PrescriptionResponse updatePrescriptionMedicine(Long prescriptionId, Long medicineId, PrescriptionMedicineRequest request);
    PrescriptionResponse deletePrescriptionMedicine(Long prescriptionId, Long medicineId);
    List<PrescriptionMedicineResponse> getPrescriptionMedicines(Long prescriptionId);

    // Dispensing Operations
    DispensingResponse dispensePrescription(Long prescriptionId, DispensingRequest request);
    List<DispensingResponse> getAllDispensings();
    DispensingResponse getDispensingById(Long id);
}
