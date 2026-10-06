package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blood_bank_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bloodGroup;

    @Column(nullable = false)
    private Integer availableUnits;

    private String status;
}
