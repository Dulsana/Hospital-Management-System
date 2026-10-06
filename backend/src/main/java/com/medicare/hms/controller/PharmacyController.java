package com.medicare.hms.controller;

import com.medicare.hms.dto.*;
import com.medicare.hms.model.*;
import com.medicare.hms.service.PharmacyManagementService;
import com.medicare.hms.service.PharmacyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
@CrossOrigin(origins = "*")
public class PharmacyController {

    private final PharmacyService pharmacyService;
    private final PharmacyManagementService legacyPharmacyService;

    @Autowired
    public PharmacyController(PharmacyService pharmacyService, PharmacyManagementService legacyPharmacyService) {
        this.pharmacyService = pharmacyService;
        this.legacyPharmacyService = legacyPharmacyService;
    }

    // ==========================================
    // 1. Medicine CRUD & Endpoints
    // ==========================================

    @PostMapping("/medicines")
    public ResponseEntity<MedicineResponse> createMedicine(@Valid @RequestBody MedicineRequest request) {
        MedicineResponse created = pharmacyService.createMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/medicines")
    public ResponseEntity<List<MedicineResponse>> getAllMedicines(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String batchNumber) {
        if (query != null || name != null || category != null || manufacturer != null || supplier != null || batchNumber != null) {
            return ResponseEntity.ok(pharmacyService.searchMedicines(query, name, category, manufacturer, supplier, batchNumber));
        }
        return ResponseEntity.ok(pharmacyService.getAllMedicines());
    }

    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getMedicineById(id));
    }

    @PutMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponse> updateMedicine(@PathVariable Long id, @RequestBody MedicineRequest request) {
        return ResponseEntity.ok(pharmacyService.updateMedicine(id, request));
    }

    @DeleteMapping("/medicines/{id}")
    public ResponseEntity<Map<String, String>> deleteMedicine(@PathVariable Long id) {
        pharmacyService.deleteMedicine(id);
        return ResponseEntity.ok(Map.of("message", "Medicine with ID " + id + " has been successfully deleted/deactivated."));
    }

    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineResponse>> searchMedicines(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) String batchNumber) {
        return ResponseEntity.ok(pharmacyService.searchMedicines(query, name, category, manufacturer, supplier, batchNumber));
    }

    @GetMapping("/medicines/low-stock")
    public ResponseEntity<List<MedicineResponse>> getLowStockMedicines() {
        return ResponseEntity.ok(pharmacyService.getLowStockMedicines());
    }

    @GetMapping("/medicines/expired")
    public ResponseEntity<List<MedicineResponse>> getExpiredMedicines() {
        return ResponseEntity.ok(pharmacyService.getExpiredMedicines());
    }

    @GetMapping("/medicines/near-expiry")
    public ResponseEntity<List<MedicineResponse>> getNearExpiryMedicines(
            @RequestParam(required = false, defaultValue = "30") Integer days) {
        return ResponseEntity.ok(pharmacyService.getNearExpiryMedicines(days));
    }

    // ==========================================
    // 2. Prescription Endpoints
    // ==========================================

    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionResponse> createPrescription(@RequestBody PrescriptionRequest request) {
        PrescriptionResponse created = pharmacyService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<List<PrescriptionResponse>> getAllPrescriptions() {
        return ResponseEntity.ok(pharmacyService.getAllPrescriptions());
    }

    @GetMapping("/prescriptions/{id}")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPrescriptionById(id));
    }

    @PutMapping("/prescriptions/{id}")
    public ResponseEntity<PrescriptionResponse> updatePrescription(@PathVariable Long id, @RequestBody PrescriptionRequest request) {
        return ResponseEntity.ok(pharmacyService.updatePrescription(id, request));
    }

    @DeleteMapping("/prescriptions/{id}")
    public ResponseEntity<Map<String, String>> deletePrescription(@PathVariable Long id) {
        pharmacyService.deletePrescription(id);
        return ResponseEntity.ok(Map.of("message", "Prescription #" + id + " has been successfully deleted."));
    }

    // ==========================================
    // 3. Prescription Medicine Items Endpoints
    // ==========================================

    @PostMapping("/prescriptions/{id}/medicines")
    public ResponseEntity<PrescriptionResponse> addMedicineToPrescription(
            @PathVariable Long id,
            @Valid @RequestBody PrescriptionMedicineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pharmacyService.addMedicineToPrescription(id, request));
    }

    @PutMapping("/prescriptions/{id}/medicines/{medicineId}")
    public ResponseEntity<PrescriptionResponse> updatePrescriptionMedicine(
            @PathVariable Long id,
            @PathVariable Long medicineId,
            @RequestBody PrescriptionMedicineRequest request) {
        return ResponseEntity.ok(pharmacyService.updatePrescriptionMedicine(id, medicineId, request));
    }

    @DeleteMapping("/prescriptions/{id}/medicines/{medicineId}")
    public ResponseEntity<PrescriptionResponse> deletePrescriptionMedicine(
            @PathVariable Long id,
            @PathVariable Long medicineId) {
        return ResponseEntity.ok(pharmacyService.deletePrescriptionMedicine(id, medicineId));
    }

    @GetMapping("/prescriptions/{id}/medicines")
    public ResponseEntity<List<PrescriptionMedicineResponse>> getPrescriptionMedicines(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPrescriptionMedicines(id));
    }

    // ==========================================
    // 4. Dispensing Endpoints
    // ==========================================

    @PostMapping("/prescriptions/{id}/dispense")
    public ResponseEntity<DispensingResponse> dispensePrescription(
            @PathVariable Long id,
            @RequestBody(required = false) DispensingRequest request) {
        if (request == null) {
            request = DispensingRequest.builder()
                    .pharmacistName("Head Pharmacist")
                    .notes("Medicine dispensed to patient")
                    .build();
        }
        return ResponseEntity.ok(pharmacyService.dispensePrescription(id, request));
    }

    @GetMapping("/dispensing")
    public ResponseEntity<List<DispensingResponse>> getAllDispensings() {
        return ResponseEntity.ok(pharmacyService.getAllDispensings());
    }

    @GetMapping("/dispensing/{id}")
    public ResponseEntity<DispensingResponse> getDispensingById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getDispensingById(id));
    }

    // ==========================================
    // 5. Dashboard, Categories & Suppliers (Compatibility)
    // ==========================================

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        return ResponseEntity.ok(legacyPharmacyService.getDashboardMetrics());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<MedicineCategory>> getAllCategories() {
        return ResponseEntity.ok(legacyPharmacyService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<MedicineCategory> createCategory(@RequestBody MedicineCategory category) {
        return ResponseEntity.ok(legacyPharmacyService.saveCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<MedicineCategory> updateCategory(@PathVariable Long id, @RequestBody MedicineCategory category) {
        return ResponseEntity.ok(legacyPharmacyService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        boolean deleted = legacyPharmacyService.deleteOrDeactivateCategory(id);
        return ResponseEntity.ok(Map.of("message", deleted
                ? "Category deleted."
                : "Category is used by medicines, so it was set to INACTIVE instead of being deleted."));
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(legacyPharmacyService.getAllSuppliers());
    }

    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(legacyPharmacyService.saveSupplier(supplier));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(legacyPharmacyService.updateSupplier(id, supplier));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable Long id) {
        boolean deleted = legacyPharmacyService.deleteOrDeactivateSupplier(id);
        return ResponseEntity.ok(Map.of("message", deleted
                ? "Supplier deleted."
                : "Supplier is used by medicines or stock, so it was set to INACTIVE instead of being deleted."));
    }

    @GetMapping("/stock")
    public ResponseEntity<List<MedicineStock>> getAllStock() {
        return ResponseEntity.ok(legacyPharmacyService.getAllStock());
    }

    @PostMapping("/stock")
    public ResponseEntity<MedicineStock> addStock(@RequestBody MedicineStock stock) {
        return ResponseEntity.ok(legacyPharmacyService.addStock(stock));
    }

    @PutMapping("/stock/{id}")
    public ResponseEntity<MedicineStock> updateStock(@PathVariable Long id, @RequestBody MedicineStock stock) {
        return ResponseEntity.ok(legacyPharmacyService.updateStock(id, stock));
    }

    @DeleteMapping("/stock/{id}")
    public ResponseEntity<Map<String, String>> deleteStock(@PathVariable Long id) {
        legacyPharmacyService.deleteStock(id);
        return ResponseEntity.ok(Map.of("message", "Stock batch #" + id + " deleted and medicine quantity updated."));
    }

    @GetMapping("/stock/history")
    public ResponseEntity<List<StockTransaction>> getStockHistory() {
        return ResponseEntity.ok(legacyPharmacyService.getStockHistory());
    }
}
