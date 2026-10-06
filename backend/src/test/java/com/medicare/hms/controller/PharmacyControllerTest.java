package com.medicare.hms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medicare.hms.dto.*;
import com.medicare.hms.exception.GlobalExceptionHandler;
import com.medicare.hms.exception.MedicineNotFoundException;
import com.medicare.hms.service.AppointmentService;
import com.medicare.hms.service.PharmacyManagementService;
import com.medicare.hms.service.PharmacyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PharmacyControllerTest {

    private MockMvc mockMvc;
    private MockMvc appointmentMockMvc;

    @Mock private PharmacyService pharmacyService;
    @Mock private PharmacyManagementService legacyPharmacyService;
    @Mock private AppointmentService appointmentService;

    @InjectMocks private PharmacyController pharmacyController;
    @InjectMocks private AppointmentController appointmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(pharmacyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        appointmentMockMvc = MockMvcBuilders.standaloneSetup(appointmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testCreateMedicine_Returns201() throws Exception {
        MedicineRequest request = MedicineRequest.builder()
                .medicineName("Paracetamol")
                .category("Painkiller")
                .quantity(100)
                .reorderLevel(20)
                .unitPrice(25.50)
                .build();

        MedicineResponse response = MedicineResponse.builder()
                .medicineId(1L)
                .medicineName("Paracetamol")
                .category("Painkiller")
                .quantity(100)
                .unitPrice(25.50)
                .status("AVAILABLE")
                .build();

        when(pharmacyService.createMedicine(any(MedicineRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pharmacy/medicines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicineId").value(1))
                .andExpect(jsonPath("$.medicineName").value("Paracetamol"));
    }

    @Test
    void testGetMedicineById_NotFound_Returns404() throws Exception {
        when(pharmacyService.getMedicineById(999L)).thenThrow(new MedicineNotFoundException(999L));

        mockMvc.perform(get("/api/pharmacy/medicines/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("MEDICINE_NOT_FOUND"));
    }

    @Test
    void testGetAppointmentPrescription_Integration() throws Exception {
        PrescriptionResponse response = PrescriptionResponse.builder()
                .prescriptionId(5001L)
                .appointmentId(101L)
                .patientId(25L)
                .doctorId(7L)
                .status("PENDING")
                .medicines(List.of(PrescriptionMedicineResponse.builder()
                        .medicineId(1L)
                        .medicineName("Paracetamol")
                        .quantity(10)
                        .dosage("500mg")
                        .frequency("3 times daily")
                        .duration("5 days")
                        .build()))
                .build();

        when(pharmacyService.getPrescriptionByAppointment(101L)).thenReturn(response);

        appointmentMockMvc.perform(get("/api/appointments/101/prescription"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prescriptionId").value(5001))
                .andExpect(jsonPath("$.appointmentId").value(101))
                .andExpect(jsonPath("$.medicines[0].medicineName").value("Paracetamol"));
    }

    @Test
    void testDispensePrescription_Returns200() throws Exception {
        DispensingRequest request = DispensingRequest.builder()
                .pharmacistId(15L)
                .notes("Medicine dispensed to patient")
                .build();

        DispensingResponse response = DispensingResponse.builder()
                .dispensingId(1L)
                .prescriptionId(5001L)
                .patientName("Kasun Perera")
                .status("DISPENSED")
                .totalAmount(255.0)
                .build();

        when(pharmacyService.dispensePrescription(eq(5001L), any(DispensingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pharmacy/prescriptions/5001/dispense")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISPENSED"))
                .andExpect(jsonPath("$.prescriptionId").value(5001));
    }
}
