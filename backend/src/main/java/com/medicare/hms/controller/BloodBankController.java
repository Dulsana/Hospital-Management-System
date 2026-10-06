package com.medicare.hms.controller;

import com.medicare.hms.model.*;
import com.medicare.hms.service.BloodBankManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bloodbank")
@CrossOrigin(origins = "*")
public class BloodBankController {

    private final BloodBankManagementService bloodBankService;

    @Autowired
    public BloodBankController(BloodBankManagementService bloodBankService) {
        this.bloodBankService = bloodBankService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(bloodBankService.getDashboardStats());
    }

    // Donors API
    @GetMapping("/donors")
    public ResponseEntity<List<BloodDonor>> getAllDonors() {
        return ResponseEntity.ok(bloodBankService.getAllDonors());
    }

    @GetMapping("/donors/{id}")
    public ResponseEntity<BloodDonor> getDonorById(@PathVariable Long id) {
        return bloodBankService.getDonorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/donors")
    public ResponseEntity<BloodDonor> registerDonor(@RequestBody BloodDonor donor) {
        return ResponseEntity.ok(bloodBankService.registerDonor(donor));
    }

    @PutMapping("/donors/{id}")
    public ResponseEntity<BloodDonor> updateDonor(@PathVariable Long id, @RequestBody BloodDonor donor) {
        return ResponseEntity.ok(bloodBankService.updateDonor(id, donor));
    }

    @DeleteMapping("/donors/{id}")
    public ResponseEntity<Map<String, String>> deactivateDonor(@PathVariable Long id) {
        boolean deleted = bloodBankService.deactivateDonor(id);
        return ResponseEntity.ok(Map.of("message", deleted
                ? "Donor deleted."
                : "Donor has donation history, so the record was set to INACTIVE instead of being deleted."));
    }

    // Donations API
    @GetMapping("/donations")
    public ResponseEntity<List<BloodDonation>> getAllDonations() {
        return ResponseEntity.ok(bloodBankService.getAllDonations());
    }

    @PostMapping("/donations")
    public ResponseEntity<?> recordDonation(@RequestBody BloodDonation donation) {
        try {
            return ResponseEntity.ok(bloodBankService.recordDonation(donation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Blood Testing API
    @GetMapping("/tests")
    public ResponseEntity<List<BloodTest>> getAllBloodTests() {
        return ResponseEntity.ok(bloodBankService.getAllBloodTests());
    }

    @PostMapping("/tests")
    public ResponseEntity<?> recordTestResult(@RequestBody BloodTest test) {
        try {
            return ResponseEntity.ok(bloodBankService.recordTestResult(test));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Blood Units API
    @GetMapping("/units")
    public ResponseEntity<List<BloodUnit>> getAllBloodUnits() {
        return ResponseEntity.ok(bloodBankService.getAllBloodUnits());
    }

    @GetMapping("/units/available")
    public ResponseEntity<List<BloodUnit>> getAvailableUnits() {
        return ResponseEntity.ok(bloodBankService.getAvailableUnits());
    }

    // Blood Requests API
    @GetMapping("/requests")
    public ResponseEntity<List<BloodRequestDetail>> getAllRequests() {
        return ResponseEntity.ok(bloodBankService.getAllRequests());
    }

    @PostMapping("/requests")
    public ResponseEntity<BloodRequestDetail> createRequest(@RequestBody BloodRequestDetail request) {
        return ResponseEntity.ok(bloodBankService.createRequest(request));
    }

    @PutMapping("/requests/{id}")
    public ResponseEntity<BloodRequestDetail> updateRequest(@PathVariable Long id, @RequestBody BloodRequestDetail request) {
        return ResponseEntity.ok(bloodBankService.updateRequest(id, request));
    }

    @DeleteMapping("/requests/{id}")
    public ResponseEntity<Map<String, String>> deleteRequest(@PathVariable Long id) {
        bloodBankService.deleteRequest(id);
        return ResponseEntity.ok(Map.of("message", "Blood request deleted."));
    }

    @PutMapping("/requests/{id}/status")
    public ResponseEntity<BloodRequestDetail> updateRequestStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(bloodBankService.updateRequestStatus(id, status));
    }

    // Blood Issues API
    @GetMapping("/issues")
    public ResponseEntity<List<BloodIssue>> getAllIssues() {
        return ResponseEntity.ok(bloodBankService.getAllIssues());
    }

    @PostMapping("/issues")
    public ResponseEntity<?> issueBloodUnit(
            @RequestParam Long requestId,
            @RequestParam Long bloodUnitId,
            @RequestParam(required = false) String issuedBy,
            @RequestParam(required = false) String notes) {
        try {
            return ResponseEntity.ok(bloodBankService.issueBloodUnit(requestId, bloodUnitId, issuedBy, notes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Inventory Transactions API
    @GetMapping("/transactions")
    public ResponseEntity<List<BloodInventoryTransaction>> getAllTransactions() {
        return ResponseEntity.ok(bloodBankService.getAllTransactions());
    }
}
