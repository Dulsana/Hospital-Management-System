package com.medicare.hms.model;

import jakarta.persistence.*;
import lombok.*;

//Tell JPA: "Create a database table for this Java class"
@Entity

//Name the database table "operation_theatres"
@Table(name = "operation_theatres")

//Lombok shortcuts: Auto-generate Getters, Setters, Constructors & Builder
@Getter
@Setter

// Empty constructor: new OperationTheatre()
@NoArgsConstructor

// Full constructor: new OperationTheatre(id, suiteName, ...)
@AllArgsConstructor

// Allows object creation like: OperationTheatre.builder().id("1").build()
@Builder
public class OperationTheatre {

    //Primary Key: Unique ID for each operating room
    @Id
    private String id;

    //Room name (e.g., "OT-1"). Cannot be empty/null in the database
    @Column(nullable = false)
    private String suiteName;

    private String leadSurgeon;

    private String currentProcedure;

    private String status;

    private String scheduledTime;
}
