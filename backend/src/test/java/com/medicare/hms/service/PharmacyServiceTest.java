package com.medicare.hms.service;

import com.medicare.hms.dto.*;
import com.medicare.hms.exception.*;
import com.medicare.hms.model.*;
import com.medicare.hms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyServiceTest {

    @Mock private MedicineRepository medicineRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private PrescriptionMedicineRepository prescriptionMedicineRepository;
    @Mock private DispensingRecordRepository dispensingRecordRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private MedicineStockRepository medicineStockRepository;

    @InjectMocks
    private PharmacyServiceImpl pharmacyService;

    private Medicine paracetamol;
    private Appointment sampleAppointment;
    private Prescription samplePrescription;

    @BeforeEach
    void setUp() {
        paracetamol = Medicine.builder()
                .medicineId(1L)
                .medicineName("Paracetamol 500mg")
                .genericName("Paracetamol")
                .categoryName("Painkiller")
                .manufacturer("ABC Pharma")
                .supplierName("XYZ Suppliers")
                .quantity(100)
                .reorderLevel(20)
                .unitPrice(25.50)
                .sellingPrice(BigDecimal.valueOf(25.50))
                .batchNumber("PCM-2026-001")
                .expiryDate(LocalDate.now().plusMonths(12))
                .dosageForm("Tablet")
                .status("AVAILABLE")
                .build();

        sampleAppointment = Appointment.builder()
                .id(101L)
                .referenceCode("MED-APPT-101")
                .patientName("John Doe")
                .patientPhone("0771234567")
                .appointmentDate(LocalDate.now())
                .timeSlot("Morning")
                .status("CONFIRMED")
                .build();

        samplePrescription = Prescription.builder()
                .prescriptionId(5001L)
                .appointmentId(101L)
                .patientId(25L)
                .patientName("John Doe")
                .doctorId(7L)
                .doctorName("Dr. Silva")
                .prescriptionDate(LocalDate.now())
                .status("PENDING")
                .items(new ArrayList<>())
                .build();
    }

    // --- 1. Medicine CRUD Tests ---

    @Test
    void testCreateMedicine_Success() {
        MedicineRequest req = MedicineRequest.builder()
                .medicineName("Amoxicillin 500mg")
                .category("Antibiotic")
                .quantity(50)
                .reorderLevel(10)
                .unitPrice(45.00)
                .batchNumber("AMX-2026-001")
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        when(medicineRepository.existsByMedicineNameIgnoreCase(anyString())).thenReturn(false);
        when(medicineRepository.save(any(Medicine.class))).thenAnswer(inv -> {
            Medicine m = inv.getArgument(0);
            m.setMedicineId(2L);
            return m;
        });

        MedicineResponse res = pharmacyService.createMedicine(req);
        assertNotNull(res);
        assertEquals(2L, res.getMedicineId());
        assertEquals("Amoxicillin 500mg", res.getMedicineName());
        assertEquals(50, res.getQuantity());
    }

    @Test
    void testGetMedicineById_NotFound() {
        when(medicineRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(MedicineNotFoundException.class, () -> pharmacyService.getMedicineById(999L));
    }

    @Test
    void testUpdateMedicine_Success() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(paracetamol));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(paracetamol);

        MedicineRequest req = MedicineRequest.builder()
                .medicineName("Paracetamol 500mg Extra")
                .quantity(120)
                .unitPrice(30.00)
                .build();

        MedicineResponse res = pharmacyService.updateMedicine(1L, req);
        assertNotNull(res);
        assertEquals(120, paracetamol.getQuantity());
        assertEquals(30.00, paracetamol.getUnitPrice());
    }

    @Test
    void testDeleteMedicine_SoftDeleteWhenUsedInPrescriptions() {
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(paracetamol));
        when(prescriptionMedicineRepository.existsByMedicine_MedicineId(1L)).thenReturn(true);

        pharmacyService.deleteMedicine(1L);
        assertEquals("INACTIVE", paracetamol.getStatus());
        verify(medicineRepository).save(paracetamol);
        verify(medicineRepository, never()).delete(any());
    }

    @Test
    void testGetLowStockMedicines() {
        when(medicineRepository.findLowStockMedicines()).thenReturn(List.of(paracetamol));
        List<MedicineResponse> lowStock = pharmacyService.getLowStockMedicines();
        assertEquals(1, lowStock.size());
    }

    @Test
    void testGetExpiredMedicines() {
        when(medicineRepository.findExpiredMedicines(any(LocalDate.class))).thenReturn(List.of());
        List<MedicineResponse> expired = pharmacyService.getExpiredMedicines();
        assertTrue(expired.isEmpty());
    }

    // --- 2. Prescription & Appointment Integration Tests ---

    @Test
    void testCreatePrescription_WithAppointment() {
        when(appointmentRepository.findById(101L)).thenReturn(Optional.of(sampleAppointment));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(inv -> {
            Prescription p = inv.getArgument(0);
            p.setPrescriptionId(5001L);
            return p;
        });

        PrescriptionRequest req = PrescriptionRequest.builder()
                .appointmentId(101L)
                .patientId(25L)
                .doctorId(7L)
                .doctorName("Dr. Silva")
                .notes("Take after meals")
                .build();

        PrescriptionResponse res = pharmacyService.createPrescription(req);
        assertNotNull(res);
        assertEquals(5001L, res.getPrescriptionId());
        assertEquals(101L, res.getAppointmentId());
        assertEquals("John Doe", res.getPatientName());
    }

    @Test
    void testGetPrescriptionByAppointment() {
        when(prescriptionRepository.findFirstByAppointmentId(101L)).thenReturn(Optional.of(samplePrescription));
        PrescriptionResponse res = pharmacyService.getPrescriptionByAppointment(101L);
        assertNotNull(res);
        assertEquals(5001L, res.getPrescriptionId());
        assertEquals(101L, res.getAppointmentId());
    }

    // --- 3. Prescription Items Tests ---

    @Test
    void testAddMedicineToPrescription_Success() {
        when(prescriptionRepository.findById(5001L)).thenReturn(Optional.of(samplePrescription));
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(paracetamol));
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(samplePrescription);

        PrescriptionMedicineRequest medReq = PrescriptionMedicineRequest.builder()
                .medicineId(1L)
                .quantity(10)
                .dosage("500mg")
                .frequency("3 times daily")
                .duration("5 days")
                .instructions("Take after meals")
                .build();

        PrescriptionResponse res = pharmacyService.addMedicineToPrescription(5001L, medReq);
        assertNotNull(res);
        assertEquals(1, samplePrescription.getItems().size());
        assertEquals(10, samplePrescription.getItems().get(0).getQuantityPrescribed());
    }

    // --- 4. Dispensing & Stock Reduction Tests ---

    @Test
    void testDispensePrescription_Success() {
        // Setup prescription with 10 units of Paracetamol (stock = 100)
        PrescriptionItem item = PrescriptionItem.builder()
                .prescriptionItemId(1L)
                .prescription(samplePrescription)
                .medicine(paracetamol)
                .quantityPrescribed(10)
                .dosage("500mg")
                .frequency("3 times daily")
                .duration("5 days")
                .build();
        samplePrescription.getItems().add(item);

        when(prescriptionRepository.findById(5001L)).thenReturn(Optional.of(samplePrescription));
        when(dispensingRecordRepository.save(any(DispensingRecord.class))).thenAnswer(inv -> {
            DispensingRecord r = inv.getArgument(0);
            r.setDispensingId(9001L);
            return r;
        });

        DispensingRequest dispReq = DispensingRequest.builder()
                .pharmacistId(15L)
                .pharmacistName("Pharmacist Jane")
                .notes("Dispensed successfully")
                .build();

        DispensingResponse res = pharmacyService.dispensePrescription(5001L, dispReq);

        assertNotNull(res);
        assertEquals("DISPENSED", res.getStatus());
        assertEquals(90, paracetamol.getQuantity()); // Stock reduced from 100 to 90
        assertEquals("DISPENSED", samplePrescription.getStatus());
        assertEquals(255.0, res.getTotalAmount()); // 10 * 25.50
        verify(medicineRepository).save(paracetamol);
        verify(dispensingRecordRepository).save(any(DispensingRecord.class));
    }

    @Test
    void testDispensePrescription_InsufficientStock_ThrowsException() {
        paracetamol.setQuantity(5); // Only 5 available
        PrescriptionItem item = PrescriptionItem.builder()
                .prescriptionItemId(1L)
                .prescription(samplePrescription)
                .medicine(paracetamol)
                .quantityPrescribed(10) // Requested 10
                .build();
        samplePrescription.getItems().add(item);

        when(prescriptionRepository.findById(5001L)).thenReturn(Optional.of(samplePrescription));

        assertThrows(InsufficientStockException.class, () ->
                pharmacyService.dispensePrescription(5001L, new DispensingRequest())
        );
        assertEquals(5, paracetamol.getQuantity()); // Stock remains unchanged
    }

    @Test
    void testDispensePrescription_ExpiredMedicine_ThrowsException() {
        paracetamol.setExpiryDate(LocalDate.now().minusDays(5)); // Expired!
        PrescriptionItem item = PrescriptionItem.builder()
                .prescriptionItemId(1L)
                .prescription(samplePrescription)
                .medicine(paracetamol)
                .quantityPrescribed(10)
                .build();
        samplePrescription.getItems().add(item);

        when(prescriptionRepository.findById(5001L)).thenReturn(Optional.of(samplePrescription));

        assertThrows(ExpiredMedicineException.class, () ->
                pharmacyService.dispensePrescription(5001L, new DispensingRequest())
        );
    }
}
