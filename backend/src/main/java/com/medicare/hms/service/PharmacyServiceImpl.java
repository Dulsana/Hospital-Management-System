package com.medicare.hms.service;

import com.medicare.hms.dto.*;
import com.medicare.hms.exception.*;
import com.medicare.hms.model.*;
import com.medicare.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PharmacyServiceImpl implements PharmacyService {

    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMedicineRepository prescriptionMedicineRepository;
    private final DispensingRecordRepository dispensingRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineStockRepository medicineStockRepository;

    @Autowired
    public PharmacyServiceImpl(
            MedicineRepository medicineRepository,
            PrescriptionRepository prescriptionRepository,
            PrescriptionMedicineRepository prescriptionMedicineRepository,
            DispensingRecordRepository dispensingRecordRepository,
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            MedicineStockRepository medicineStockRepository) {
        this.medicineRepository = medicineRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMedicineRepository = prescriptionMedicineRepository;
        this.dispensingRecordRepository = dispensingRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.medicineStockRepository = medicineStockRepository;
    }

    // ==========================================
    // 1. Medicine CRUD & Queries
    // ==========================================

    @Override
    @Transactional
    public MedicineResponse createMedicine(MedicineRequest request) {
        if (request.getMedicineName() == null || request.getMedicineName().isBlank()) {
            throw new IllegalArgumentException("Medicine name is required");
        }
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (medicineRepository.existsByMedicineNameIgnoreCase(request.getMedicineName().trim())) {
            throw new DuplicateMedicineException("Medicine with name '" + request.getMedicineName() + "' already exists.");
        }

        Double price = request.getUnitPrice() != null ? request.getUnitPrice() : 0.0;
        if (price < 0) {
            throw new IllegalArgumentException("Unit price must be >= 0");
        }
        Integer qty = request.getQuantity() != null ? request.getQuantity() : 0;
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0");
        }
        Integer reorder = request.getReorderLevel() != null ? request.getReorderLevel() : 10;
        if (reorder < 0) {
            throw new IllegalArgumentException("Reorder level must be >= 0");
        }

        Medicine medicine = Medicine.builder()
                .medicineName(request.getMedicineName().trim())
                .genericName(request.getGenericName() != null ? request.getGenericName() : request.getMedicineName())
                .categoryName(request.getCategory().trim())
                .manufacturer(request.getManufacturer())
                .supplierName(request.getSupplier())
                .description(request.getDescription())
                .dosageForm(request.getDosageForm() != null ? request.getDosageForm() : "Tablet")
                .strength(request.getStrength())
                .unit(request.getUnit() != null ? request.getUnit() : "Unit")
                .quantity(qty)
                .reorderLevel(reorder)
                .unitPrice(price)
                .sellingPrice(BigDecimal.valueOf(price))
                .batchNumber(request.getBatchNumber())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus() != null && !request.getStatus().isBlank() ? request.getStatus() : "AVAILABLE")
                .build();

        Medicine saved = medicineRepository.save(medicine);
        return mapToMedicineResponse(saved);
    }

    @Override
    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll().stream()
                .map(this::mapToMedicineResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));
        return mapToMedicineResponse(medicine);
    }

    @Override
    @Transactional
    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));

        if (request.getMedicineName() != null && !request.getMedicineName().isBlank()) {
            String newName = request.getMedicineName().trim();
            if (medicineRepository.existsByMedicineNameIgnoreCaseAndMedicineIdNot(newName, id)) {
                throw new DuplicateMedicineException("Another medicine with name '" + newName + "' already exists.");
            }
            medicine.setMedicineName(newName);
        }

        if (request.getCategory() != null) medicine.setCategoryName(request.getCategory().trim());
        if (request.getGenericName() != null) medicine.setGenericName(request.getGenericName());
        if (request.getDescription() != null) medicine.setDescription(request.getDescription());
        if (request.getManufacturer() != null) medicine.setManufacturer(request.getManufacturer());
        if (request.getSupplier() != null) medicine.setSupplierName(request.getSupplier());
        if (request.getDosageForm() != null) medicine.setDosageForm(request.getDosageForm());
        if (request.getStrength() != null) medicine.setStrength(request.getStrength());
        if (request.getUnit() != null) medicine.setUnit(request.getUnit());

        if (request.getQuantity() != null) {
            if (request.getQuantity() < 0) throw new IllegalArgumentException("Quantity must be >= 0");
            medicine.setQuantity(request.getQuantity());
        }
        if (request.getReorderLevel() != null) {
            if (request.getReorderLevel() < 0) throw new IllegalArgumentException("Reorder level must be >= 0");
            medicine.setReorderLevel(request.getReorderLevel());
        }
        if (request.getUnitPrice() != null) {
            if (request.getUnitPrice() < 0) throw new IllegalArgumentException("Unit price must be >= 0");
            medicine.setUnitPrice(request.getUnitPrice());
            medicine.setSellingPrice(BigDecimal.valueOf(request.getUnitPrice()));
        }
        if (request.getBatchNumber() != null) medicine.setBatchNumber(request.getBatchNumber());
        if (request.getExpiryDate() != null) medicine.setExpiryDate(request.getExpiryDate());
        if (request.getStatus() != null) medicine.setStatus(request.getStatus());

        Medicine updated = medicineRepository.save(medicine);
        return mapToMedicineResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException(id));

        // Check whether medicine is used in existing prescriptions or stock batches
        boolean isUsed = prescriptionMedicineRepository.existsByMedicine_MedicineId(id)
                || medicineStockRepository.existsByMedicine_MedicineId(id);
        if (isUsed) {
            // Soft delete to protect historical clinical and dispensing records
            medicine.setStatus("INACTIVE");
            medicineRepository.save(medicine);
        } else {
            medicineRepository.delete(medicine);
        }
    }

    @Override
    public List<MedicineResponse> searchMedicines(String query, String name, String category, String manufacturer, String supplier, String batchNumber) {
        List<Medicine> results = medicineRepository.searchMedicinesAdvanced(
                query != null && !query.isBlank() ? query.trim() : null,
                name != null && !name.isBlank() ? name.trim() : null,
                category != null && !category.isBlank() ? category.trim() : null,
                manufacturer != null && !manufacturer.isBlank() ? manufacturer.trim() : null,
                supplier != null && !supplier.isBlank() ? supplier.trim() : null,
                batchNumber != null && !batchNumber.isBlank() ? batchNumber.trim() : null
        );
        return results.stream().map(this::mapToMedicineResponse).collect(Collectors.toList());
    }

    @Override
    public List<MedicineResponse> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines().stream()
                .map(this::mapToMedicineResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineResponse> getExpiredMedicines() {
        return medicineRepository.findExpiredMedicines(LocalDate.now()).stream()
                .map(this::mapToMedicineResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineResponse> getNearExpiryMedicines(Integer days) {
        int window = (days != null && days > 0) ? days : 30;
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(window);
        return medicineRepository.findNearExpiryMedicines(from, to).stream()
                .map(this::mapToMedicineResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // 2. Prescription Operations & Appointment Integration
    // ==========================================

    @Override
    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        String patientName = request.getPatientName();
        String doctorName = request.getDoctorName();
        Long doctorId = request.getDoctorId();
        Long patientId = request.getPatientId();

        // Verify appointment if specified
        if (request.getAppointmentId() != null) {
            Optional<Appointment> apptOpt = appointmentRepository.findById(request.getAppointmentId());
            if (apptOpt.isEmpty()) {
                throw new AppointmentNotFoundException(request.getAppointmentId());
            }
            Appointment appt = apptOpt.get();
            if (patientName == null || patientName.isBlank()) {
                patientName = appt.getPatientName();
            }
            if (doctorName == null || doctorName.isBlank()) {
                if (appt.getDoctor() != null) {
                    doctorName = appt.getDoctor().getName();
                    doctorId = appt.getDoctor().getId();
                }
            }
        }

        if (patientName == null || patientName.isBlank()) {
            patientName = "Walk-in Patient";
        }
        if (doctorName == null || doctorName.isBlank()) {
            doctorName = "Attending Physician";
        }

        Prescription prescription = Prescription.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(patientId)
                .patientName(patientName)
                .doctorId(doctorId)
                .doctorName(doctorName)
                .prescriptionDate(request.getPrescriptionDate() != null ? request.getPrescriptionDate() : LocalDate.now())
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .items(new ArrayList<>())
                .build();

        Prescription saved = prescriptionRepository.save(prescription);

        if (request.getMedicines() != null && !request.getMedicines().isEmpty()) {
            for (PrescriptionMedicineRequest medReq : request.getMedicines()) {
                addMedicineInternal(saved, medReq);
            }
            saved = prescriptionRepository.save(saved);
        }

        return mapToPrescriptionResponse(saved);
    }

    @Override
    public List<PrescriptionResponse> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::mapToPrescriptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));
        return mapToPrescriptionResponse(prescription);
    }

    @Override
    @Transactional
    public PrescriptionResponse updatePrescription(Long id, PrescriptionRequest request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));

        if (request.getPatientName() != null && !request.getPatientName().isBlank()) {
            prescription.setPatientName(request.getPatientName());
        }
        if (request.getDoctorName() != null && !request.getDoctorName().isBlank()) {
            prescription.setDoctorName(request.getDoctorName());
        }
        if (request.getDiagnosis() != null) prescription.setDiagnosis(request.getDiagnosis());
        if (request.getNotes() != null) prescription.setNotes(request.getNotes());
        if (request.getStatus() != null) prescription.setStatus(request.getStatus());
        if (request.getPrescriptionDate() != null) prescription.setPrescriptionDate(request.getPrescriptionDate());

        Prescription updated = prescriptionRepository.save(prescription);
        return mapToPrescriptionResponse(updated);
    }

    @Override
    @Transactional
    public void deletePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException(id));
        
        if ("DISPENSED".equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Cannot delete a prescription that has already been dispensed.");
        }
        prescriptionRepository.delete(prescription);
    }

    @Override
    public PrescriptionResponse getPrescriptionByAppointment(Long appointmentId) {
        // First check by ID
        Optional<Prescription> pOpt = prescriptionRepository.findFirstByAppointmentId(appointmentId);
        if (pOpt.isPresent()) {
            return mapToPrescriptionResponse(pOpt.get());
        }

        // Verify if appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(appointmentId));

        // Return empty or create on-the-fly DTO
        return PrescriptionResponse.builder()
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatientName())
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .doctorName(appointment.getDoctor() != null ? appointment.getDoctor().getName() : null)
                .prescriptionDate(appointment.getAppointmentDate())
                .status("PENDING")
                .medicines(new ArrayList<>())
                .build();
    }

    // ==========================================
    // 3. Prescription Medicine Items
    // ==========================================

    @Override
    @Transactional
    public PrescriptionResponse addMedicineToPrescription(Long prescriptionId, PrescriptionMedicineRequest request) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        if ("DISPENSED".equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Cannot modify medicines in an already dispensed prescription.");
        }

        addMedicineInternal(prescription, request);
        Prescription saved = prescriptionRepository.save(prescription);
        return mapToPrescriptionResponse(saved);
    }

    private void addMedicineInternal(Prescription prescription, PrescriptionMedicineRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException(request.getMedicineId()));

        if (medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ExpiredMedicineException("Medicine '" + medicine.getMedicineName() + "' has expired on " + medicine.getExpiryDate() + " and cannot be prescribed.");
        }

        if ("INACTIVE".equalsIgnoreCase(medicine.getStatus())) {
            throw new InvalidPrescriptionException("Medicine '" + medicine.getMedicineName() + "' is inactive/unavailable.");
        }

        PrescriptionItem item = PrescriptionItem.builder()
                .prescription(prescription)
                .medicine(medicine)
                .quantityPrescribed(request.getQuantity())
                .dosage(request.getDosage() != null ? request.getDosage() : "1 tablet")
                .frequency(request.getFrequency() != null ? request.getFrequency() : "Daily")
                .duration(request.getDuration() != null ? request.getDuration() : "5 days")
                .instructions(request.getInstructions())
                .build();

        prescription.getItems().add(item);
    }

    @Override
    @Transactional
    public PrescriptionResponse updatePrescriptionMedicine(Long prescriptionId, Long medicineId, PrescriptionMedicineRequest request) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        if ("DISPENSED".equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Cannot update medicines on an already dispensed prescription.");
        }

        PrescriptionItem targetItem = prescription.getItems().stream()
                .filter(item -> item.getMedicine() != null && item.getMedicine().getMedicineId().equals(medicineId))
                .findFirst()
                .orElseThrow(() -> new MedicineNotFoundException("Medicine with ID " + medicineId + " not found in prescription " + prescriptionId));

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
            targetItem.setQuantityPrescribed(request.getQuantity());
        }
        if (request.getDosage() != null) targetItem.setDosage(request.getDosage());
        if (request.getFrequency() != null) targetItem.setFrequency(request.getFrequency());
        if (request.getDuration() != null) targetItem.setDuration(request.getDuration());
        if (request.getInstructions() != null) targetItem.setInstructions(request.getInstructions());

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToPrescriptionResponse(saved);
    }

    @Override
    @Transactional
    public PrescriptionResponse deletePrescriptionMedicine(Long prescriptionId, Long medicineId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        if ("DISPENSED".equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Cannot remove medicines from an already dispensed prescription.");
        }

        boolean removed = prescription.getItems().removeIf(item ->
                item.getMedicine() != null && item.getMedicine().getMedicineId().equals(medicineId));

        if (!removed) {
            throw new MedicineNotFoundException("Medicine with ID " + medicineId + " not found in prescription " + prescriptionId);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToPrescriptionResponse(saved);
    }

    @Override
    public List<PrescriptionMedicineResponse> getPrescriptionMedicines(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        return prescription.getItems().stream()
                .map(this::mapToPrescriptionMedicineResponse)
                .collect(Collectors.toList());
    }

    // ==========================================
    // 4. Dispensing & Automatic Stock Reduction
    // ==========================================

    @Override
    @Transactional
    public DispensingResponse dispensePrescription(Long prescriptionId, DispensingRequest request) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new PrescriptionNotFoundException(prescriptionId));

        if ("CANCELLED".equalsIgnoreCase(prescription.getStatus())) {
            throw new InvalidPrescriptionException("Cancelled prescriptions cannot be dispensed.");
        }
        if ("DISPENSED".equalsIgnoreCase(prescription.getStatus())) {
            throw new InvalidPrescriptionException("Prescription #" + prescriptionId + " has already been fully dispensed.");
        }
        if (prescription.getItems() == null || prescription.getItems().isEmpty()) {
            throw new InvalidPrescriptionException("Prescription #" + prescriptionId + " has no medicines to dispense.");
        }

        Double totalAmount = 0.0;
        List<PrescriptionMedicineResponse> dispensedItems = new ArrayList<>();

        // 1. Verify stock and expiry for all medicines first
        for (PrescriptionItem item : prescription.getItems()) {
            Medicine medicine = item.getMedicine();
            int requiredQty = item.getQuantityPrescribed();

            if (medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(LocalDate.now())) {
                throw new ExpiredMedicineException("Cannot dispense: Medicine '" + medicine.getMedicineName() + "' is expired (" + medicine.getExpiryDate() + ").");
            }

            int currentStock = medicine.getQuantity() != null ? medicine.getQuantity() : 0;
            if (currentStock < requiredQty) {
                throw new InsufficientStockException("Only " + currentStock + " units of " + medicine.getMedicineName() + " are available.");
            }
        }

        // 2. Deduct stock atomically
        for (PrescriptionItem item : prescription.getItems()) {
            Medicine medicine = item.getMedicine();
            int requiredQty = item.getQuantityPrescribed();

            medicine.setQuantity(medicine.getQuantity() - requiredQty);
            if (medicine.getQuantity() == 0) {
                medicine.setStatus("DEPLETED");
            }
            medicineRepository.save(medicine);

            Double unitPrice = medicine.getEffectiveUnitPrice();
            Double subtotal = unitPrice * requiredQty;
            totalAmount += subtotal;

            dispensedItems.add(PrescriptionMedicineResponse.builder()
                    .prescriptionItemId(item.getPrescriptionItemId())
                    .medicineId(medicine.getMedicineId())
                    .medicineName(medicine.getMedicineName())
                    .quantity(requiredQty)
                    .dosage(item.getDosage())
                    .frequency(item.getFrequency())
                    .duration(item.getDuration())
                    .instructions(item.getInstructions())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }

        // 3. Update prescription status
        prescription.setStatus("DISPENSED");
        prescriptionRepository.save(prescription);

        // 4. Save dispensing record
        Long pharmacistId = (request != null && request.getPharmacistId() != null) ? request.getPharmacistId() : 1L;
        String pharmacistName = (request != null && request.getPharmacistName() != null && !request.getPharmacistName().isBlank())
                ? request.getPharmacistName()
                : "Head Pharmacist";
        String notes = (request != null) ? request.getNotes() : null;

        DispensingRecord record = DispensingRecord.builder()
                .prescriptionId(prescriptionId)
                .patientName(prescription.getPatientName())
                .pharmacistId(pharmacistId)
                .pharmacistName(pharmacistName)
                .dispensingDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .status("DISPENSED")
                .notes(notes)
                .build();

        DispensingRecord savedRecord = dispensingRecordRepository.save(record);

        return DispensingResponse.builder()
                .dispensingId(savedRecord.getDispensingId())
                .prescriptionId(prescriptionId)
                .patientName(prescription.getPatientName())
                .pharmacistId(pharmacistId)
                .pharmacistName(pharmacistName)
                .dispensingDate(savedRecord.getDispensingDate())
                .totalAmount(totalAmount)
                .status("DISPENSED")
                .notes(notes)
                .items(dispensedItems)
                .build();
    }

    @Override
    public List<DispensingResponse> getAllDispensings() {
        return dispensingRecordRepository.findAllByOrderByDispensingIdDesc().stream()
                .map(r -> DispensingResponse.builder()
                        .dispensingId(r.getDispensingId())
                        .prescriptionId(r.getPrescriptionId())
                        .patientName(r.getPatientName())
                        .pharmacistId(r.getPharmacistId())
                        .pharmacistName(r.getPharmacistName())
                        .dispensingDate(r.getDispensingDate())
                        .totalAmount(r.getTotalAmount())
                        .status(r.getStatus())
                        .notes(r.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public DispensingResponse getDispensingById(Long id) {
        DispensingRecord r = dispensingRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispensing record with ID " + id + " not found."));

        return DispensingResponse.builder()
                .dispensingId(r.getDispensingId())
                .prescriptionId(r.getPrescriptionId())
                .patientName(r.getPatientName())
                .pharmacistId(r.getPharmacistId())
                .pharmacistName(r.getPharmacistName())
                .dispensingDate(r.getDispensingDate())
                .totalAmount(r.getTotalAmount())
                .status(r.getStatus())
                .notes(r.getNotes())
                .build();
    }

    // ==========================================
    // Mapping Helpers
    // ==========================================

    private MedicineResponse mapToMedicineResponse(Medicine m) {
        return MedicineResponse.builder()
                .medicineId(m.getMedicineId())
                .medicineName(m.getMedicineName())
                .category(m.getCategoryDisplayName())
                .description(m.getDescription())
                .manufacturer(m.getManufacturer())
                .supplier(m.getSupplierDisplayName())
                .quantity(m.getQuantity() != null ? m.getQuantity() : 0)
                .reorderLevel(m.getReorderLevel() != null ? m.getReorderLevel() : 10)
                .unitPrice(m.getEffectiveUnitPrice())
                .batchNumber(m.getBatchNumber())
                .expiryDate(m.getExpiryDate())
                .dosageForm(m.getDosageForm())
                .status(m.getStatus())
                .genericName(m.getGenericName())
                .strength(m.getStrength())
                .unit(m.getUnit())
                .build();
    }

    private PrescriptionResponse mapToPrescriptionResponse(Prescription p) {
        List<PrescriptionMedicineResponse> items = new ArrayList<>();
        if (p.getItems() != null) {
            items = p.getItems().stream()
                    .map(this::mapToPrescriptionMedicineResponse)
                    .collect(Collectors.toList());
        }

        return PrescriptionResponse.builder()
                .prescriptionId(p.getPrescriptionId())
                .appointmentId(p.getAppointmentId())
                .patientId(p.getPatientId())
                .patientName(p.getPatientName())
                .doctorId(p.getDoctorId())
                .doctorName(p.getDoctorName())
                .prescriptionDate(p.getPrescriptionDate())
                .diagnosis(p.getDiagnosis())
                .notes(p.getNotes())
                .status(p.getStatus())
                .medicines(items)
                .build();
    }

    private PrescriptionMedicineResponse mapToPrescriptionMedicineResponse(PrescriptionItem item) {
        Double unitPrice = (item.getMedicine() != null) ? item.getMedicine().getEffectiveUnitPrice() : 0.0;
        int qty = item.getQuantityPrescribed() != null ? item.getQuantityPrescribed() : 0;
        return PrescriptionMedicineResponse.builder()
                .prescriptionItemId(item.getPrescriptionItemId())
                .medicineId(item.getMedicine() != null ? item.getMedicine().getMedicineId() : null)
                .medicineName(item.getMedicine() != null ? item.getMedicine().getMedicineName() : "Unknown Medicine")
                .quantity(qty)
                .dosage(item.getDosage())
                .frequency(item.getFrequency())
                .duration(item.getDuration())
                .instructions(item.getInstructions())
                .unitPrice(unitPrice)
                .subtotal(unitPrice * qty)
                .build();
    }
}
