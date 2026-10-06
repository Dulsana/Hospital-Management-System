package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Ward & Bed Management module (merged from the team member's HospitalManagementSystem project).
 * The ward ID is chosen by staff, e.g. "WD-ICU".
 */
@Entity
@Table(name = "ward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {

    @Id
    @Column(name = "ward_id", length = 30)
    private String wardId;

    @Column(name = "ward_number", nullable = false)
    private Integer wardNumber;

    @Column(name = "ward_type", nullable = false)
    private String wardType;
}
