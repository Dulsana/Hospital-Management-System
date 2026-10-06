package com.medicare.hms.controller;

import com.medicare.hms.model.*;
import com.medicare.hms.service.DoctorSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor-scheduling")
@CrossOrigin(origins = "*")
public class DoctorSchedulingController {

    private final DoctorSchedulingService schedulingService;

    @Autowired
    public DoctorSchedulingController(DoctorSchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(schedulingService.getDashboardMetrics());
    }

    // Specializations
    @GetMapping("/specializations")
    public ResponseEntity<List<DoctorSpecialization>> getAllSpecializations() {
        return ResponseEntity.ok(schedulingService.getAllSpecializations());
    }

    @PostMapping("/specializations")
    public ResponseEntity<DoctorSpecialization> createSpecialization(@RequestBody DoctorSpecialization spec) {
        return ResponseEntity.ok(schedulingService.createSpecialization(spec));
    }

    @PutMapping("/specializations/{id}")
    public ResponseEntity<DoctorSpecialization> updateSpecialization(@PathVariable Long id, @RequestBody DoctorSpecialization spec) {
        return ResponseEntity.ok(schedulingService.updateSpecialization(id, spec));
    }

    // Availability
    @GetMapping("/availability")
    public ResponseEntity<List<DoctorAvailability>> getAllAvailability() {
        return ResponseEntity.ok(schedulingService.getAllAvailability());
    }

    @PutMapping("/availability/{id}")
    public ResponseEntity<DoctorAvailability> updateAvailability(@PathVariable Long id, @RequestBody DoctorAvailability availability) {
        return ResponseEntity.ok(schedulingService.updateAvailability(id, availability));
    }

    @GetMapping("/availability/{doctorId}")
    public ResponseEntity<List<DoctorAvailability>> getAvailabilityByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(schedulingService.getAvailabilityByDoctor(doctorId));
    }

    @PostMapping("/availability")
    public ResponseEntity<DoctorAvailability> createAvailability(@RequestBody DoctorAvailability availability) {
        return ResponseEntity.ok(schedulingService.createAvailability(availability));
    }

    // Schedules
    @GetMapping("/schedules")
    public ResponseEntity<List<DoctorWorkSchedule>> getAllSchedules() {
        return ResponseEntity.ok(schedulingService.getAllSchedules());
    }

    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody DoctorWorkSchedule schedule) {
        try {
            return ResponseEntity.ok(schedulingService.createSchedule(schedule));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<DoctorWorkSchedule> updateSchedule(@PathVariable Long id, @RequestBody DoctorWorkSchedule schedule) {
        return ResponseEntity.ok(schedulingService.updateSchedule(id, schedule));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Map<String, String>> deleteSchedule(@PathVariable Long id) {
        schedulingService.deleteSchedule(id);
        return ResponseEntity.ok(Map.of("message", "Schedule and its consultation slots deleted successfully"));
    }

    // Shifts & Rosters
    @PutMapping("/shifts/{id}")
    public ResponseEntity<Shift> updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        return ResponseEntity.ok(schedulingService.updateShift(id, shift));
    }

    @PutMapping("/rosters/{id}")
    public ResponseEntity<DoctorRoster> updateRoster(@PathVariable Long id, @RequestBody DoctorRoster roster) {
        return ResponseEntity.ok(schedulingService.updateRoster(id, roster));
    }

    @GetMapping("/shifts")
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(schedulingService.getAllShifts());
    }

    @PostMapping("/shifts")
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        return ResponseEntity.ok(schedulingService.createShift(shift));
    }

    @GetMapping("/rosters")
    public ResponseEntity<List<DoctorRoster>> getAllRosters() {
        return ResponseEntity.ok(schedulingService.getAllRosters());
    }

    @PostMapping("/rosters")
    public ResponseEntity<?> createRoster(@RequestBody DoctorRoster roster) {
        try {
            return ResponseEntity.ok(schedulingService.createRoster(roster));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Consultation Slots
    @GetMapping("/slots")
    public ResponseEntity<List<ConsultationSlot>> getSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(schedulingService.getSlotsByDoctorAndDate(doctorId, date));
    }

    @PostMapping("/slots/{slotId}/book")
    public ResponseEntity<?> bookSlot(@PathVariable Long slotId) {
        try {
            return ResponseEntity.ok(schedulingService.bookSlot(slotId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Breaks
    @GetMapping("/breaks")
    public ResponseEntity<List<DoctorBreak>> getAllBreaks() {
        return ResponseEntity.ok(schedulingService.getAllBreaks());
    }

    @PostMapping("/breaks")
    public ResponseEntity<DoctorBreak> createBreak(@RequestBody DoctorBreak drBreak) {
        return ResponseEntity.ok(schedulingService.createBreak(drBreak));
    }

    // Leave
    @GetMapping("/leave")
    public ResponseEntity<List<DoctorLeave>> getAllLeave() {
        return ResponseEntity.ok(schedulingService.getAllLeave());
    }

    @PostMapping("/leave")
    public ResponseEntity<DoctorLeave> applyLeave(@RequestBody DoctorLeave leave) {
        return ResponseEntity.ok(schedulingService.applyLeave(leave));
    }

    @PutMapping("/leave/{leaveId}")
    public ResponseEntity<DoctorLeave> updateLeave(@PathVariable Long leaveId, @RequestBody DoctorLeave leave) {
        return ResponseEntity.ok(schedulingService.updateLeave(leaveId, leave));
    }

    @PutMapping("/leave/{leaveId}/status")
    public ResponseEntity<DoctorLeave> updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam String status,
            @RequestParam(required = false, defaultValue = "Admin") String approvedBy) {
        return ResponseEntity.ok(schedulingService.updateLeaveStatus(leaveId, status, approvedBy));
    }

    // Delete endpoints for full CRUD support
    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<Map<String, String>> deleteSpecialization(@PathVariable Long id) {
        schedulingService.deleteSpecialization(id);
        return ResponseEntity.ok(Map.of("message", "Specialization deleted successfully"));
    }

    @DeleteMapping("/availability/{id}")
    public ResponseEntity<Map<String, String>> deleteAvailability(@PathVariable Long id) {
        schedulingService.deleteAvailability(id);
        return ResponseEntity.ok(Map.of("message", "Availability deleted successfully"));
    }

    @PutMapping("/schedules/{id}/cancel")
    public ResponseEntity<DoctorWorkSchedule> cancelSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(schedulingService.cancelSchedule(id));
    }

    @DeleteMapping("/shifts/{id}")
    public ResponseEntity<Map<String, String>> deleteShift(@PathVariable Long id) {
        schedulingService.deleteShift(id);
        return ResponseEntity.ok(Map.of("message", "Shift deleted successfully"));
    }

    @DeleteMapping("/rosters/{id}")
    public ResponseEntity<Map<String, String>> deleteRoster(@PathVariable Long id) {
        schedulingService.deleteRoster(id);
        return ResponseEntity.ok(Map.of("message", "Roster deleted successfully"));
    }

    @DeleteMapping("/breaks/{id}")
    public ResponseEntity<Map<String, String>> deleteBreak(@PathVariable Long id) {
        schedulingService.deleteBreak(id);
        return ResponseEntity.ok(Map.of("message", "Break deleted successfully"));
    }

    @DeleteMapping("/leave/{id}")
    public ResponseEntity<Map<String, String>> deleteLeave(@PathVariable Long id) {
        schedulingService.deleteLeave(id);
        return ResponseEntity.ok(Map.of("message", "Leave deleted successfully"));
    }
}
