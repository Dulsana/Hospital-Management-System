package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dispensings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispensing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispensing_id")
    private Long dispensingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(name = "pharmacist_name")
    private String pharmacistName;

    @Column(name = "dispensing_date", insertable = false, updatable = false)
    private LocalDateTime dispensingDate;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "dispensing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DispensingItem> items;
}
