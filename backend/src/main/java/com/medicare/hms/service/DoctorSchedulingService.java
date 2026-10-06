package com.medicare.hms.service;

import com.medicare.hms.model.*;
import com.medicare.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorSchedulingService {

    private final DoctorRepository doctorRepository;
    private final DoctorSpecializationRepository specializationRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorWorkScheduleRepository scheduleRepository;
    private final ShiftRepository shiftRepository;
    private final DoctorRosterRepository rosterRepository;
    private final ConsultationSlotRepository slotRepository;
    private final DoctorBreakRepository breakRepository;
    private final DoctorLeaveRepository leaveRepository;

    @Autowired
    public DoctorSchedulingService(
            DoctorRepository doctorRepository,
            DoctorSpecializationRepository specializationRepository,
            DoctorAvailabilityRepository availabilityRepository,
            DoctorWorkScheduleRepository scheduleRepository,
            ShiftRepository shiftRepository,
            DoctorRosterRepository rosterRepository,
            ConsultationSlotRepository slotRepository,
            DoctorBreakRepository breakRepository,
            DoctorLeaveRepository leaveRepository) {
        this.doctorRepository = doctorRepository;
        this.specializationRepository = specializationRepository;
        this.availabilityRepository = availabilityRepository;
        this.scheduleRepository = scheduleRepository;
        this.shiftRepository = shiftRepository;
        this.rosterRepository = rosterRepository;
        this.slotRepository = slotRepository;
        this.breakRepository = breakRepository;
        this.leaveRepository = leaveRepository;
    }

    // Specializations
    public List<DoctorSpecialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }

    public DoctorSpecialization createSpecialization(DoctorSpecialization spec) {
        requireText(spec.getName(), "Specialization name is required.");
        return specializationRepository.save(spec);
    }

    @Transactional
    public DoctorSpecialization updateSpecialization(Long id, DoctorSpecialization changes) {
        DoctorSpecialization spec = specializationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Specialization not found with ID: " + id));
        requireText(changes.getName(), "Specialization name is required.");
        spec.setName(changes.getName().trim());
        spec.setDescription(changes.getDescription());
        if (changes.getStatus() != null) spec.setStatus(changes.getStatus());
        return specializationRepository.save(spec);
    }

    // Availability
    public List<DoctorAvailability> getAvailabilityByDoctor(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    public List<DoctorAvailability> getAllAvailability() {
        return availabilityRepository.findAll();
    }

    @Transactional
    public DoctorAvailability createAvailability(DoctorAvailability availability) {
        requireDoctor(availability.getDoctorId());
        validateTimes(availability.getStartTime(), availability.getEndTime());
        return availabilityRepository.save(availability);
    }

    @Transactional
    public DoctorAvailability updateAvailability(Long id, DoctorAvailability changes) {
        DoctorAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Availability record not found with ID: " + id));
        requireDoctor(changes.getDoctorId());
        validateTimes(changes.getStartTime(), changes.getEndTime());
        availability.setDoctorId(changes.getDoctorId());
        availability.setDayOfWeek(changes.getDayOfWeek());
        availability.setStartTime(changes.getStartTime());
        availability.setEndTime(changes.getEndTime());
        if (changes.getAvailabilityStatus() != null) availability.setAvailabilityStatus(changes.getAvailabilityStatus());
        availability.setNotes(changes.getNotes());
        return availabilityRepository.save(availability);
    }

    // Schedules
    public List<DoctorWorkSchedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Transactional
    public DoctorWorkSchedule createSchedule(DoctorWorkSchedule schedule) {
        requireDoctor(schedule.getDoctorId());
        if (schedule.getScheduleDate() == null) throw new IllegalArgumentException("Schedule date is required.");
        validateTimes(schedule.getStartTime(), schedule.getEndTime());
        // 1. Check doctor leave conflict
        List<DoctorLeave> leaves = leaveRepository.findByDoctorIdAndStatus(schedule.getDoctorId(), "APPROVED");
        for (DoctorLeave leave : leaves) {
            if (!schedule.getScheduleDate().isBefore(leave.getStartDate()) && !schedule.getScheduleDate().isAfter(leave.getEndDate())) {
                throw new RuntimeException("Doctor is on approved leave during date: " + schedule.getScheduleDate());
            }
        }

        // 2. Check doctor overlapping schedule conflict
        List<DoctorWorkSchedule> existingSchedules = scheduleRepository.findByDoctorIdAndScheduleDate(
                schedule.getDoctorId(), schedule.getScheduleDate());
        for (DoctorWorkSchedule s : existingSchedules) {
            if (!"CANCELLED".equalsIgnoreCase(s.getStatus())) {
                if (isTimeOverlapping(schedule.getStartTime(), schedule.getEndTime(), s.getStartTime(), s.getEndTime())) {
                    throw new RuntimeException("Doctor already has a schedule during this time (" + s.getStartTime() + " - " + s.getEndTime() + ").");
                }
            }
        }

        // Save schedule
        DoctorWorkSchedule savedSchedule = scheduleRepository.save(schedule);

        // Auto generate consultation slots (e.g., 30 mins)
        generateConsultationSlots(savedSchedule, 30);

        return savedSchedule;
    }

    // Helper for slot generation
    private void generateConsultationSlots(DoctorWorkSchedule schedule, int durationMins) {
        try {
            LocalTime start = LocalTime.parse(schedule.getStartTime());
            LocalTime end = LocalTime.parse(schedule.getEndTime());

            while (start.plusMinutes(durationMins).isBefore(end) || start.plusMinutes(durationMins).equals(end)) {
                LocalTime slotEnd = start.plusMinutes(durationMins);
                
                ConsultationSlot slot = new ConsultationSlot();
                slot.setScheduleId(schedule.getId());
                slot.setDoctorId(schedule.getDoctorId());
                slot.setSlotDate(schedule.getScheduleDate());
                slot.setStartTime(start.toString());
                slot.setEndTime(slotEnd.toString());
                slot.setDurationMinutes(durationMins);
                slot.setRoomNumber(schedule.getRoomNumber());
                slot.setStatus("AVAILABLE");

                slotRepository.save(slot);
                start = slotEnd;
            }
        } catch (Exception e) {
            System.err.println("Error generating slots: " + e.getMessage());
        }
    }

    // Shifts & Rosters
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public Shift createShift(Shift shift) {
        requireText(shift.getShiftName(), "Shift name is required.");
        validateTimes(shift.getStartTime(), shift.getEndTime());
        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift updateShift(Long id, Shift changes) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift not found with ID: " + id));
        requireText(changes.getShiftName(), "Shift name is required.");
        validateTimes(changes.getStartTime(), changes.getEndTime());
        shift.setShiftName(changes.getShiftName().trim());
        shift.setStartTime(changes.getStartTime());
        shift.setEndTime(changes.getEndTime());
        if (changes.getShiftType() != null) shift.setShiftType(changes.getShiftType());
        shift.setDescription(changes.getDescription());
        if (changes.getStatus() != null) shift.setStatus(changes.getStatus());
        return shiftRepository.save(shift);
    }

    public List<DoctorRoster> getAllRosters() {
        return rosterRepository.findAll();
    }

    @Transactional
    public DoctorRoster createRoster(DoctorRoster roster) {
        validateRoster(roster);
        return rosterRepository.save(roster);
    }

    @Transactional
    public DoctorRoster updateRoster(Long id, DoctorRoster changes) {
        DoctorRoster roster = rosterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Roster entry not found with ID: " + id));
        validateRoster(changes);
        roster.setDoctorId(changes.getDoctorId());
        roster.setShiftId(changes.getShiftId());
        roster.setRosterDate(changes.getRosterDate());
        roster.setRoomNumber(changes.getRoomNumber());
        if (changes.getStatus() != null) roster.setStatus(changes.getStatus());
        roster.setNotes(changes.getNotes());
        return rosterRepository.save(roster);
    }

    private void validateRoster(DoctorRoster roster) {
        requireDoctor(roster.getDoctorId());
        if (roster.getShiftId() == null || !shiftRepository.existsById(roster.getShiftId())) {
            throw new IllegalArgumentException("Please select a valid shift.");
        }
        if (roster.getRosterDate() == null) throw new IllegalArgumentException("Roster date is required.");
        // A doctor cannot be rostered while on approved leave
        List<DoctorLeave> leaves = leaveRepository.findByDoctorIdAndStatus(roster.getDoctorId(), "APPROVED");
        for (DoctorLeave leave : leaves) {
            if (!roster.getRosterDate().isBefore(leave.getStartDate()) && !roster.getRosterDate().isAfter(leave.getEndDate())) {
                throw new IllegalStateException("Doctor is on approved leave on date: " + roster.getRosterDate());
            }
        }
    }

    // Consultation Slots
    public List<ConsultationSlot> getSlotsByDoctorAndDate(Long doctorId, LocalDate date) {
        return slotRepository.findByDoctorIdAndSlotDate(doctorId, date);
    }

    @Transactional
    public synchronized ConsultationSlot bookSlot(Long slotId) {
        ConsultationSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (!"AVAILABLE".equalsIgnoreCase(slot.getStatus())) {
            throw new RuntimeException("Sorry, this consultation slot is already " + slot.getStatus());
        }
        slot.setStatus("BOOKED");
        return slotRepository.save(slot);
    }

    // Breaks & Leave
    public List<DoctorBreak> getAllBreaks() {
        return breakRepository.findAll();
    }

    public DoctorBreak createBreak(DoctorBreak drBreak) {
        return breakRepository.save(drBreak);
    }

    public List<DoctorLeave> getAllLeave() {
        return leaveRepository.findAll();
    }

    @Transactional
    public DoctorLeave applyLeave(DoctorLeave leave) {
        validateLeave(leave);
        return leaveRepository.save(leave);
    }

    // Edit a leave request while it is still waiting for approval
    @Transactional
    public DoctorLeave updateLeave(Long id, DoctorLeave changes) {
        DoctorLeave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave record not found with ID: " + id));
        if (!"PENDING".equalsIgnoreCase(leave.getStatus())) {
            throw new IllegalStateException("Only PENDING leave requests can be edited. This one is " + leave.getStatus() + ".");
        }
        validateLeave(changes);
        leave.setDoctorId(changes.getDoctorId());
        leave.setLeaveType(changes.getLeaveType());
        leave.setStartDate(changes.getStartDate());
        leave.setEndDate(changes.getEndDate());
        leave.setReason(changes.getReason());
        return leaveRepository.save(leave);
    }

    private void validateLeave(DoctorLeave leave) {
        requireDoctor(leave.getDoctorId());
        requireText(leave.getLeaveType(), "Leave type is required.");
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required.");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before the start date.");
        }
    }

    @Transactional
    public DoctorLeave updateLeaveStatus(Long leaveId, String status, String approvedBy) {
        DoctorLeave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave record not found with ID: " + leaveId));
        leave.setStatus(status);
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(java.time.LocalDateTime.now());
        return leaveRepository.save(leave);
    }

    // Conflict detection helper
    private boolean isTimeOverlapping(String startAStr, String endAStr, String startBStr, String endBStr) {
        LocalTime startA = LocalTime.parse(startAStr);
        LocalTime endA = LocalTime.parse(endAStr);
        LocalTime startB = LocalTime.parse(startBStr);
        LocalTime endB = LocalTime.parse(endBStr);

        return startA.isBefore(endB) && endA.isAfter(startB);
    }

    // Delete & Status update helpers for complete CRUD
    public void deleteSpecialization(Long id) {
        specializationRepository.deleteById(id);
    }

    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }

    @Transactional
    public DoctorWorkSchedule cancelSchedule(Long id) {
        DoctorWorkSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + id));
        schedule.setStatus("CANCELLED");
        // Also update associated slots
        List<ConsultationSlot> slots = slotRepository.findByDoctorIdAndSlotDate(schedule.getDoctorId(), schedule.getScheduleDate());
        for (ConsultationSlot slot : slots) {
            if (slot.getScheduleId().equals(id)) {
                slot.setStatus("CANCELLED");
                slotRepository.save(slot);
            }
        }
        return scheduleRepository.save(schedule);
    }

    // Room / type / notes can be edited; the date and times stay fixed because
    // consultation slots were generated from them.
    @Transactional
    public DoctorWorkSchedule updateSchedule(Long id, DoctorWorkSchedule changes) {
        DoctorWorkSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + id));
        if (changes.getRoomNumber() != null) schedule.setRoomNumber(changes.getRoomNumber());
        if (changes.getScheduleType() != null) schedule.setScheduleType(changes.getScheduleType());
        schedule.setNotes(changes.getNotes());
        for (ConsultationSlot slot : slotRepository.findByScheduleId(id)) {
            slot.setRoomNumber(schedule.getRoomNumber());
            slotRepository.save(slot);
        }
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        DoctorWorkSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found with ID: " + id));
        List<ConsultationSlot> slots = slotRepository.findByScheduleId(id);
        boolean hasBookings = slots.stream().anyMatch(sl -> "BOOKED".equalsIgnoreCase(sl.getStatus()));
        if (hasBookings) {
            throw new IllegalStateException("This schedule has booked consultation slots. Cancel it instead of deleting.");
        }
        slotRepository.deleteAll(slots);
        scheduleRepository.delete(schedule);
    }

    private void requireDoctor(Long doctorId) {
        if (doctorId == null || !doctorRepository.existsById(doctorId)) {
            throw new IllegalArgumentException("Please select a valid doctor.");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }

    // Times are stored as "HH:mm" text; check the format before saving so the
    // overlap check and slot generation never fail later.
    private void validateTimes(String start, String end) {
        try {
            LocalTime s = LocalTime.parse(start);
            LocalTime e = LocalTime.parse(end);
            if (!s.isBefore(e)) {
                throw new IllegalArgumentException("Start time must be before end time.");
            }
        } catch (java.time.format.DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("Times must be in HH:mm format, for example 08:00.");
        }
    }

    public void deleteShift(Long id) {
        shiftRepository.deleteById(id);
    }

    public void deleteRoster(Long id) {
        rosterRepository.deleteById(id);
    }

    public void deleteBreak(Long id) {
        breakRepository.deleteById(id);
    }

    public void deleteLeave(Long id) {
        leaveRepository.deleteById(id);
    }

    // Dashboard metrics
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        long totalDoctors = doctorRepository.count();
        long availableToday = scheduleRepository.findByScheduleDate(LocalDate.now()).size();
        long onLeaveToday = leaveRepository.findByStatus("APPROVED").stream()
                .filter(l -> !LocalDate.now().isBefore(l.getStartDate()) && !LocalDate.now().isAfter(l.getEndDate()))
                .count();
        long todaySchedules = scheduleRepository.findByScheduleDate(LocalDate.now()).size();
        long availableSlots = slotRepository.findAll().stream()
                .filter(s -> "AVAILABLE".equalsIgnoreCase(s.getStatus())).count();

        metrics.put("totalDoctors", totalDoctors);
        metrics.put("availableToday", availableToday);
        metrics.put("onLeaveToday", onLeaveToday);
        metrics.put("todaySchedules", todaySchedules);
        metrics.put("availableSlots", availableSlots);

        return metrics;
    }
}
