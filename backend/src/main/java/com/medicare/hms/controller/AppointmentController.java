package com.medicare.hms.controller;

import com.medicare.hms.dto.PrescriptionResponse;
import com.medicare.hms.model.Appointment;
import com.medicare.hms.service.AppointmentService;
import com.medicare.hms.service.PharmacyService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PharmacyService pharmacyService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, PharmacyService pharmacyService) {
        this.appointmentService = appointmentService;
        this.pharmacyService = pharmacyService;
    }

    // GET /api/appointments              -> all appointments (staff, doctor, admin)
    // GET /api/appointments?email=a@b.com -> only that patient's appointments
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments(@RequestParam(required = false) String email) {
        if (email != null && !email.isBlank()) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByPatientEmail(email));
        }
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{referenceCode}")
    public ResponseEntity<Appointment> getAppointmentByRef(@PathVariable String referenceCode) {
        return appointmentService.getAppointmentByReference(referenceCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{appointmentId}/prescription")
    public ResponseEntity<PrescriptionResponse> getPrescriptionByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(pharmacyService.getPrescriptionByAppointment(appointmentId));
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.createAppointment(
                request.getDoctorId(),
                request.getPatientName(),
                request.getPatientPhone(),
                request.getPatientEmail(),
                request.getAppointmentDate() != null ? request.getAppointmentDate() : LocalDate.now().plusDays(1),
                request.getTimeSlot() != null ? request.getTimeSlot() : "Evening (05:00 PM - 08:00 PM)",
                request.getReferenceCode()
        );
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(
                id,
                request.getDoctorId(),
                request.getPatientName(),
                request.getPatientPhone(),
                request.getPatientEmail(),
                request.getAppointmentDate(),
                request.getTimeSlot()
        ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(Map.of("message", "Appointment #" + id + " deleted successfully."));
    }

    @Data
    public static class AppointmentRequest {
        private Long doctorId;
        private String doctorName;
        private String patientName;
        private String patientPhone;
        private String patientEmail;
        private LocalDate appointmentDate;
        private String timeSlot;
        private String referenceCode;
    }
}
