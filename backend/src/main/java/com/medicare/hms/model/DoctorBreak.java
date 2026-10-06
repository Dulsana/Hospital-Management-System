package com.medicare.hms.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "doctor_breaks")
public class DoctorBreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "break_date", nullable = false)
    private LocalDate breakDate;

    @Column(name = "start_time", nullable = false, length = 20)
    private String startTime;

    @Column(name = "end_time", nullable = false, length = 20)
    private String endTime;

    @Column(name = "break_type", length = 30)
    private String breakType = "LUNCH";

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    public DoctorBreak() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

    public LocalDate getBreakDate() { return breakDate; }
    public void setBreakDate(LocalDate breakDate) { this.breakDate = breakDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getBreakType() { return breakType; }
    public void setBreakType(String breakType) { this.breakType = breakType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
