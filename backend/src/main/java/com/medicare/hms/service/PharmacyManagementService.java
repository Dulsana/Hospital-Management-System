package com.medicare.hms.service;

import com.medicare.hms.util.PhoneValidator;
import com.medicare.hms.model.*;
import com.medicare.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PharmacyManagementService {

    @Autowired private MedicineCategoryRepository categoryRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private MedicineStockRepository stockRepository;
    @Autowired private StockTransactionRepository transactionRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private DispensingRepository dispensingRepository;

    // --- 1. Category Operations ---
    public List<MedicineCategory> getAllCategories() { return categoryRepository.findAll(); }
    public MedicineCategory saveCategory(MedicineCategory category) {
        if (category.getStatus() == null) category.setStatus("ACTIVE");
        return categoryRepository.save(category);
    }
    public MedicineCategory updateCategory(Long id, MedicineCategory updated) {
        if (updated.getCategoryName() == null || updated.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        return categoryRepository.findById(id).map(c -> {
            c.setCategoryName(updated.getCategoryName().trim());
            c.setDescription(updated.getDescription());
            if (updated.getStatus() != null) c.setStatus(updated.getStatus());
            return categoryRepository.save(c);
        }).orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
    }
    /** Deletes the category, or only deactivates it when medicines still point to it. Returns true if deleted. */
    public boolean deleteOrDeactivateCategory(Long id) {
        MedicineCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));
        if (medicineRepository.existsByCategory_CategoryId(id)) {
            c.setStatus("INACTIVE");
            categoryRepository.save(c);
            return false;
        }
        categoryRepository.delete(c);
        return true;
    }

    // --- 2. Supplier Operations ---
    public List<Supplier> getAllSuppliers() { return supplierRepository.findAll(); }
    public Supplier saveSupplier(Supplier supplier) {
        if (supplier.getSupplierName() == null || supplier.getSupplierName().isBlank()) {
            throw new IllegalArgumentException("Supplier name is required");
        }
        supplier.setPhone(PhoneValidator.requireValid(supplier.getPhone(), "Supplier phone"));
        if (supplier.getStatus() == null) supplier.setStatus("ACTIVE");
        return supplierRepository.save(supplier);
    }
    public Supplier updateSupplier(Long id, Supplier updated) {
        if (updated.getSupplierName() == null || updated.getSupplierName().isBlank()) {
            throw new IllegalArgumentException("Supplier name is required");
        }
        updated.setPhone(PhoneValidator.requireValid(updated.getPhone(), "Supplier phone"));
        return supplierRepository.findById(id).map(s -> {
            s.setSupplierName(updated.getSupplierName().trim());
            s.setContactPerson(updated.getContactPerson());
            s.setPhone(updated.getPhone());
            s.setEmail(updated.getEmail());
            s.setAddress(updated.getAddress());
            if (updated.getStatus() != null) s.setStatus(updated.getStatus());
            return supplierRepository.save(s);
        }).orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + id));
    }
    /** Deletes the supplier, or only deactivates it when medicines/stock still point to it. Returns true if deleted. */
    public boolean deleteOrDeactivateSupplier(Long id) {
        Supplier s = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + id));
        if (medicineRepository.existsBySupplier_SupplierId(id) || stockRepository.existsBySupplier_SupplierId(id)) {
            s.setStatus("INACTIVE");
            supplierRepository.save(s);
            return false;
        }
        supplierRepository.delete(s);
        return true;
    }

    // --- 3. Medicine Operations ---
    public List<Medicine> searchAndFilterMedicines(String query, Long categoryId, Long supplierId, String status) {
        return medicineRepository.filterMedicines(query, categoryId, supplierId, status);
    }
    public Optional<Medicine> getMedicineById(Long id) { return medicineRepository.findById(id); }
    public Medicine saveMedicine(Medicine medicine) {
        if (medicine.getSellingPrice() == null || medicine.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Selling price must be >= 0");
        }
        if (medicine.getReorderLevel() == null || medicine.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level must be >= 0");
        }
        if (medicine.getStatus() == null) medicine.setStatus("ACTIVE");
        return medicineRepository.save(medicine);
    }
    public Medicine updateMedicine(Long id, Medicine updated) {
        return medicineRepository.findById(id).map(m -> {
            m.setMedicineName(updated.getMedicineName());
            m.setGenericName(updated.getGenericName());
            m.setCategory(updated.getCategory());
            m.setSupplier(updated.getSupplier());
            m.setDescription(updated.getDescription());
            m.setDosageForm(updated.getDosageForm());
            m.setStrength(updated.getStrength());
            m.setUnit(updated.getUnit());
            m.setSellingPrice(updated.getSellingPrice());
            m.setReorderLevel(updated.getReorderLevel());
            m.setStatus(updated.getStatus());
            return medicineRepository.save(m);
        }).orElseThrow(() -> new RuntimeException("Medicine not found"));
    }
    public void deleteOrDeactivateMedicine(Long id) {
        medicineRepository.findById(id).ifPresent(m -> {
            m.setStatus("INACTIVE");
            medicineRepository.save(m);
        });
    }

    // --- 4. Stock & Transaction Operations ---
    public List<MedicineStock> getAllStock() { return stockRepository.findAll(); }

    @Transactional
    public MedicineStock addStock(MedicineStock stock) {
        validateStock(stock);
        // Load the real medicine/supplier rows (the page only sends their IDs)
        Medicine medicine = findMedicine(stock.getMedicine());
        stock.setMedicine(medicine);
        stock.setSupplier(findSupplier(stock.getSupplier()));
        if (stock.getStatus() == null) stock.setStatus("ACTIVE");
        MedicineStock savedStock = stockRepository.save(stock);

        // Dispensing reads medicine.quantity, so every new batch adds to it
        adjustMedicineQuantity(medicine, stock.getQuantity());

        // Transaction log
        StockTransaction tx = StockTransaction.builder()
                .medicine(stock.getMedicine())
                .stock(savedStock)
                .transactionType("STOCK_IN")
                .quantity(stock.getQuantity())
                .referenceId("BATCH-" + stock.getBatchNumber())
                .performedBy("Pharmacist")
                .notes("New Stock-In Added")
                .build();
        transactionRepository.save(tx);

        return savedStock;
    }

    @Transactional
    public MedicineStock updateStock(Long id, MedicineStock updated) {
        MedicineStock existing = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock batch not found with ID: " + id));
        validateStock(updated);

        Medicine oldMedicine = existing.getMedicine();
        Medicine newMedicine = findMedicine(updated.getMedicine());
        int oldQty = existing.getQuantity();
        int newQty = updated.getQuantity();

        // Move the quantity between medicines if the batch was assigned to a different one
        if (!oldMedicine.getMedicineId().equals(newMedicine.getMedicineId())) {
            adjustMedicineQuantity(oldMedicine, -oldQty);
            adjustMedicineQuantity(newMedicine, newQty);
        } else if (newQty != oldQty) {
            adjustMedicineQuantity(newMedicine, newQty - oldQty);
        }

        existing.setMedicine(newMedicine);
        existing.setBatchNumber(updated.getBatchNumber().trim());
        existing.setQuantity(newQty);
        existing.setUnitCost(updated.getUnitCost());
        existing.setExpiryDate(updated.getExpiryDate());
        existing.setReceivedDate(updated.getReceivedDate());
        existing.setSupplier(findSupplier(updated.getSupplier()));
        if (updated.getStatus() != null) existing.setStatus(updated.getStatus());
        MedicineStock saved = stockRepository.save(existing);

        if (newQty != oldQty) {
            transactionRepository.save(StockTransaction.builder()
                    .medicine(newMedicine)
                    .stock(saved)
                    .transactionType("ADJUSTMENT")
                    .quantity(newQty - oldQty)
                    .referenceId("BATCH-" + saved.getBatchNumber())
                    .performedBy("Pharmacist")
                    .notes("Stock batch corrected from " + oldQty + " to " + newQty)
                    .build());
        }
        return saved;
    }

    @Transactional
    public void deleteStock(Long id) {
        MedicineStock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stock batch not found with ID: " + id));
        // Remove what is left of this batch from the medicine total
        adjustMedicineQuantity(stock.getMedicine(), -stock.getQuantity());
        transactionRepository.deleteAll(transactionRepository.findByStock_StockId(id));
        stockRepository.delete(stock);
    }

    private void validateStock(MedicineStock stock) {
        if (stock.getMedicine() == null || stock.getMedicine().getMedicineId() == null) {
            throw new IllegalArgumentException("Please select a medicine");
        }
        if (stock.getBatchNumber() == null || stock.getBatchNumber().isBlank()) {
            throw new IllegalArgumentException("Batch number is required");
        }
        if (stock.getQuantity() == null || stock.getQuantity() <= 0) {
            throw new IllegalArgumentException("Stock quantity must be > 0");
        }
        if (stock.getUnitCost() == null || stock.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit cost must be >= 0");
        }
        if (stock.getExpiryDate() == null || stock.getReceivedDate() == null) {
            throw new IllegalArgumentException("Expiry date and received date are required");
        }
    }

    private Medicine findMedicine(Medicine ref) {
        return medicineRepository.findById(ref.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found with ID: " + ref.getMedicineId()));
    }

    private Supplier findSupplier(Supplier ref) {
        if (ref == null || ref.getSupplierId() == null) return null;
        return supplierRepository.findById(ref.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with ID: " + ref.getSupplierId()));
    }

    private void adjustMedicineQuantity(Medicine medicine, int change) {
        int current = medicine.getQuantity() != null ? medicine.getQuantity() : 0;
        int updated = Math.max(0, current + change);
        medicine.setQuantity(updated);
        if (updated > 0 && "DEPLETED".equalsIgnoreCase(medicine.getStatus())) {
            medicine.setStatus("AVAILABLE");
        } else if (updated == 0 && !"INACTIVE".equalsIgnoreCase(medicine.getStatus())) {
            medicine.setStatus("DEPLETED");
        }
        medicineRepository.save(medicine);
    }

    public List<StockTransaction> getStockHistory() {
        return transactionRepository.findAllByOrderByTransactionIdDesc();
    }

    // --- 5. Prescription Operations ---
    public List<Prescription> getAllPrescriptions() { return prescriptionRepository.findAll(); }
    public Optional<Prescription> getPrescriptionById(Long id) { return prescriptionRepository.findById(id); }

    @Transactional
    public Prescription savePrescription(Prescription prescription) {
        if (prescription.getStatus() == null) prescription.setStatus("PENDING");
        if (prescription.getPrescriptionDate() == null) prescription.setPrescriptionDate(LocalDate.now());
        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                item.setPrescription(prescription);
            }
        }
        return prescriptionRepository.save(prescription);
    }

    @Transactional
    public Prescription cancelPrescription(Long id) {
        return prescriptionRepository.findById(id).map(p -> {
            p.setStatus("CANCELLED");
            return prescriptionRepository.save(p);
        }).orElseThrow(() -> new RuntimeException("Prescription not found"));
    }

    // --- 6. Dispensing & Automatic Stock Reduction (Transactional) ---
    public List<Dispensing> getAllDispensings() { return dispensingRepository.findAllByOrderByDispensingIdDesc(); }

    @Transactional
    public Dispensing dispensePrescription(Long prescriptionId, String pharmacistName, String notes) {
        Prescription p = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));

        if ("CANCELLED".equalsIgnoreCase(p.getStatus())) {
            throw new IllegalStateException("Cancelled prescriptions cannot be dispensed.");
        }
        if ("DISPENSED".equalsIgnoreCase(p.getStatus())) {
            throw new IllegalStateException("Prescription has already been fully dispensed.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<DispensingItem> dispensingItems = new ArrayList<>();

        for (PrescriptionItem pItem : p.getItems()) {
            Medicine med = pItem.getMedicine();
            int requiredQty = pItem.getQuantityPrescribed();

            // Find available active non-expired stock batch
            List<MedicineStock> activeStocks = stockRepository.findByMedicine_MedicineIdAndStatus(med.getMedicineId(), "ACTIVE");
            MedicineStock targetStock = activeStocks.stream()
                    .filter(s -> s.getExpiryDate().isAfter(LocalDate.now()) && s.getQuantity() >= requiredQty)
                    .findFirst()
                    .orElse(null);

            if (targetStock == null) {
                throw new IllegalStateException("Insufficient non-expired stock for medicine: " + med.getMedicineName());
            }

            // Reduce stock
            targetStock.setQuantity(targetStock.getQuantity() - requiredQty);
            if (targetStock.getQuantity() == 0) {
                targetStock.setStatus("DEPLETED");
            }
            stockRepository.save(targetStock);

            // Record transaction
            StockTransaction tx = StockTransaction.builder()
                    .medicine(med)
                    .stock(targetStock)
                    .transactionType("DISPENSE")
                    .quantity(-requiredQty)
                    .referenceId("RX-" + p.getPrescriptionId())
                    .performedBy(pharmacistName)
                    .notes("Dispensed for prescription #" + p.getPrescriptionId())
                    .build();
            transactionRepository.save(tx);

            // Calculate Item Cost
            BigDecimal subtotal = med.getSellingPrice().multiply(BigDecimal.valueOf(requiredQty));
            totalAmount = totalAmount.add(subtotal);

            DispensingItem dItem = DispensingItem.builder()
                    .medicine(med)
                    .stock(targetStock)
                    .quantityDispensed(requiredQty)
                    .unitPrice(med.getSellingPrice())
                    .subtotal(subtotal)
                    .build();
            dispensingItems.add(dItem);
        }

        // Update Prescription Status
        p.setStatus("DISPENSED");
        prescriptionRepository.save(p);

        // Save Dispensing Record
        Dispensing dispensing = Dispensing.builder()
                .prescription(p)
                .patientName(p.getPatientName())
                .pharmacistName(pharmacistName != null ? pharmacistName : "Head Pharmacist")
                .totalAmount(totalAmount)
                .status("DISPENSED")
                .notes(notes)
                .items(dispensingItems)
                .build();

        for (DispensingItem item : dispensingItems) {
            item.setDispensing(dispensing);
        }

        return dispensingRepository.save(dispensing);
    }

    // --- 7. Dashboard Metrics ---
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        long totalMedicines = medicineRepository.count();
        List<MedicineStock> allStock = stockRepository.findAll();

        int totalStockQty = allStock.stream().mapToInt(MedicineStock::getQuantity).sum();
        long lowStockCount = medicineRepository.findAll().stream()
                .filter(m -> {
                    Integer curr = stockRepository.getTotalAvailableQuantityForMedicine(m.getMedicineId());
                    return curr == null || curr <= m.getReorderLevel();
                }).count();

        long expiredCount = allStock.stream().filter(s -> s.getExpiryDate().isBefore(LocalDate.now())).count();
        long expiringSoonCount = allStock.stream()
                .filter(s -> !s.getExpiryDate().isBefore(LocalDate.now()) && s.getExpiryDate().isBefore(LocalDate.now().plusDays(30)))
                .count();

        long pendingPrescriptions = prescriptionRepository.findByStatus("PENDING").size();
        List<Dispensing> todayDispensings = dispensingRepository.findByDispensingDateAfter(LocalDateTime.now().withHour(0).withMinute(0));
        BigDecimal todaySales = todayDispensings.stream().map(Dispensing::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        metrics.put("totalMedicines", totalMedicines);
        metrics.put("totalStock", totalStockQty);
        metrics.put("lowStock", lowStockCount);
        metrics.put("expiredMedicines", expiredCount);
        metrics.put("expiringSoon", expiringSoonCount);
        metrics.put("pendingPrescriptions", pendingPrescriptions);
        metrics.put("todayDispensings", todayDispensings.size());
        metrics.put("todaySales", todaySales);

        return metrics;
    }
}
