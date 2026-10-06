package com.medicare.hms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "blood_types")
public class BloodTypeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blood_group", nullable = false, unique = true, length = 10)
    private String bloodGroup;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "minimum_stock_level")
    private Integer minimumStockLevel = 10;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE";

    public BloodTypeInfo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(Integer minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
