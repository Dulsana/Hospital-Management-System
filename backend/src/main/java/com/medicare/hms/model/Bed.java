package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * A bed inside a ward (Ward & Bed Management module).
 * Status is one of: Available, Occupied, Reserved, Maintenance.
 */
@Entity
@Table(name = "bed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed {

    @Id
    @Column(name = "bed_id", length = 30)
    private String bedId;

    @Column(name = "bed_number", nullable = false)
    private Integer bedNumber;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "ward_id", nullable = false, length = 30)
    private String wardId;

    // Optional: who is in the bed when it is Occupied or Reserved
    @Column(name = "patient_name")
    private String patientName;
}
