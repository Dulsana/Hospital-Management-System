package com.medicare.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dispensing_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispensingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispensing_item_id")
    private Long dispensingItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    @JsonIgnore
    private Dispensing dispensing;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id")
    private MedicineStock stock;

    @Column(name = "quantity_dispensed", nullable = false)
    private Integer quantityDispensed;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal subtotal;
}
