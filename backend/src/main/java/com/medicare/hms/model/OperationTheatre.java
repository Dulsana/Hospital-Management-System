package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operation_theatres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationTheatre {

    @Id
    private String id;

    @Column(nullable = false)
    private String suiteName;

    private String leadSurgeon;

    private String currentProcedure;

    private String status;

    private String scheduledTime;
}
