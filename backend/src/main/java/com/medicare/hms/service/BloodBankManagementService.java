package com.medicare.hms.service;

import com.medicare.hms.util.PhoneValidator;
import com.medicare.hms.model.*;
import com.medicare.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BloodBankManagementService {

    private final BloodDonorRepository donorRepository;
    private final BloodDonationRepository donationRepository;
    private final BloodUnitRepository unitRepository;
    private final BloodTestRepository testRepository;
    private final BloodRequestDetailRepository requestRepository;
    private final BloodIssueRepository issueRepository;
    private final BloodInventoryTransactionRepository transactionRepository;
    private final BloodTypeInfoRepository bloodTypeInfoRepository;

    @Autowired
    public BloodBankManagementService(
            BloodDonorRepository donorRepository,
            BloodDonationRepository donationRepository,
            BloodUnitRepository unitRepository,
            BloodTestRepository testRepository,
            BloodRequestDetailRepository requestRepository,
            BloodIssueRepository issueRepository,
            BloodInventoryTransactionRepository transactionRepository,
            BloodTypeInfoRepository bloodTypeInfoRepository) {
        this.donorRepository = donorRepository;
        this.donationRepository = donationRepository;
        this.unitRepository = unitRepository;
        this.testRepository = testRepository;
        this.requestRepository = requestRepository;
        this.issueRepository = issueRepository;
        this.transactionRepository = transactionRepository;
        this.bloodTypeInfoRepository = bloodTypeInfoRepository;
    }

    // Dashboard metrics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalDonors = donorRepository.count();
        long totalAvailableUnits = unitRepository.findByStatus("AVAILABLE").size();
        long pendingRequests = requestRepository.findByStatus("PENDING").size();

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayDonations = donationRepository.findAll().stream()
                .filter(d -> d.getDonationDate().isAfter(todayStart)).count();
        long todayIssues = issueRepository.findAll().stream()
                .filter(i -> i.getIssueDate().isAfter(todayStart)).count();

        LocalDateTime threshold30Days = LocalDateTime.now().plusDays(30);
        long expiringSoon = unitRepository.findByExpiryDateBetweenAndStatus(LocalDateTime.now(), threshold30Days, "AVAILABLE").size();
        long expiredUnits = unitRepository.findByExpiryDateBeforeAndStatusNot(LocalDateTime.now(), "DISCARDED").size();

        // Calculate available stock per blood group
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        Map<String, Integer> stockMap = new HashMap<>();
        List<String> lowStockGroups = new ArrayList<>();

        for (String bg : groups) {
            int availableCount = unitRepository.findByBloodTypeAndStatus(bg, "AVAILABLE").size();
            stockMap.put(bg, availableCount);

            BloodTypeInfo info = bloodTypeInfoRepository.findByBloodGroup(bg).orElse(null);
            int minLevel = (info != null && info.getMinimumStockLevel() != null) ? info.getMinimumStockLevel() : 10;
            if (availableCount < minLevel) {
                lowStockGroups.add(bg);
            }
        }

        stats.put("totalDonors", totalDonors);
        stats.put("totalBloodUnits", totalAvailableUnits);
        stats.put("pendingRequests", pendingRequests);
        stats.put("todayDonations", todayDonations);
        stats.put("todayIssues", todayIssues);
        stats.put("expiringSoon", expiringSoon);
        stats.put("expiredUnits", expiredUnits);
        stats.put("stockMap", stockMap);
        stats.put("lowStockGroups", lowStockGroups);

        return stats;
    }

    // Donor CRUD
    public List<BloodDonor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Optional<BloodDonor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }

    @Transactional
    public BloodDonor registerDonor(BloodDonor donor) {
        validateDonor(donor);
        if (donor.getDonorNumber() == null || donor.getDonorNumber().isEmpty()) {
            donor.setDonorNumber(String.format("DON-%05d", nextNumber(donorRepository.findTopByOrderByIdDesc().map(BloodDonor::getId))));
        }
        return donorRepository.save(donor);
    }

    @Transactional
    public BloodDonor updateDonor(Long id, BloodDonor updatedDonor) {
        BloodDonor existing = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found with ID: " + id));
        validateDonor(updatedDonor);
        if (updatedDonor.getDateOfBirth() != null) existing.setDateOfBirth(updatedDonor.getDateOfBirth());
        if (updatedDonor.getGender() != null) existing.setGender(updatedDonor.getGender());
        existing.setFirstName(updatedDonor.getFirstName());
        existing.setLastName(updatedDonor.getLastName());
        existing.setPhone(updatedDonor.getPhone());
        existing.setEmail(updatedDonor.getEmail());
        existing.setAddress(updatedDonor.getAddress());
        existing.setCity(updatedDonor.getCity());
        if (updatedDonor.getBloodType() != null) existing.setBloodType(updatedDonor.getBloodType());
        if (updatedDonor.getEligibilityStatus() != null) existing.setEligibilityStatus(updatedDonor.getEligibilityStatus());
        if (updatedDonor.getStatus() != null) existing.setStatus(updatedDonor.getStatus());
        return donorRepository.save(existing);
    }

    /** Deletes the donor, or only deactivates them when they have donation history. Returns true if deleted. */
    @Transactional
    public boolean deactivateDonor(Long id) {
        BloodDonor donor = donorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found with ID: " + id));
        List<BloodDonation> donations = donationRepository.findByDonorId(id);
        if (!donations.isEmpty()) {
            donor.setStatus("INACTIVE");
            donorRepository.save(donor);
            return false;
        }
        donorRepository.delete(donor);
        return true;
    }

    private void validateDonor(BloodDonor donor) {
        if (donor.getFirstName() == null || donor.getFirstName().isBlank()
                || donor.getLastName() == null || donor.getLastName().isBlank()) {
            throw new IllegalArgumentException("Donor first and last name are required.");
        }
        donor.setPhone(PhoneValidator.requireValid(donor.getPhone(), "Donor phone number"));
        if (donor.getBloodType() == null || donor.getBloodType().isBlank()) {
            throw new IllegalArgumentException("Blood type is required.");
        }
    }

    // Next running number based on the newest row's ID. Unlike count()+1 this never
    // repeats a number that is still in use after a record has been deleted.
    private long nextNumber(Optional<Long> latestId) {
        return latestId.orElse(0L) + 1;
    }

    // Donation CRUD
    public List<BloodDonation> getAllDonations() {
        return donationRepository.findAll();
    }

    @Transactional
    public BloodDonation recordDonation(BloodDonation donation) {
        BloodDonor donor = donorRepository.findById(donation.getDonorId())
                .orElseThrow(() -> new RuntimeException("Donor not found"));
        
        if (!"ACTIVE".equals(donor.getStatus()) || !"ELIGIBLE".equals(donor.getEligibilityStatus())) {
            throw new RuntimeException("Donor is not eligible or active for blood donation");
        }

        long donationCount = nextNumber(donationRepository.findTopByOrderByIdDesc().map(BloodDonation::getId));
        donation.setDonationNumber(String.format("DONATION-%04d", donationCount));
        donation.setDonationDate(LocalDateTime.now());
        donation.setBloodType(donor.getBloodType());
        donation.setStatus("TESTING");

        BloodDonation savedDonation = donationRepository.save(donation);

        // Auto create quarantined blood unit
        long unitCount = nextNumber(unitRepository.findTopByOrderByIdDesc().map(BloodUnit::getId));
        BloodUnit unit = new BloodUnit();
        unit.setUnitNumber(String.format("BU-%d-%06d", LocalDateTime.now().getYear(), unitCount));
        unit.setDonationId(savedDonation.getId());
        unit.setDonorId(donor.getId());
        unit.setBloodType(donor.getBloodType());
        unit.setCollectionDate(LocalDateTime.now());
        unit.setExpiryDate(LocalDateTime.now().plusDays(42)); // Standard 42 days red blood cell shelf life
        unit.setVolumeMl(savedDonation.getVolumeMl());
        unit.setStatus("QUARANTINED");
        unit.setStorageLocation("Quarantine Vault Q1");

        BloodUnit savedUnit = unitRepository.save(unit);

        // Record stock transaction
        BloodInventoryTransaction tx = new BloodInventoryTransaction();
        tx.setBloodUnitId(savedUnit.getId());
        tx.setTransactionType("DONATION");
        tx.setQuantity(1);
        tx.setReferenceId(savedDonation.getDonationNumber());
        tx.setPerformedBy("Blood Bank Staff");
        tx.setNotes("Donation intake, unit placed in Quarantine");
        transactionRepository.save(tx);

        return savedDonation;
    }

    // Blood Testing
    public List<BloodTest> getAllBloodTests() {
        return testRepository.findAll();
    }

    @Transactional
    public BloodTest recordTestResult(BloodTest test) {
        BloodUnit unit = unitRepository.findById(test.getBloodUnitId())
                .orElseThrow(() -> new RuntimeException("Blood unit not found"));

        test.setTestDate(LocalDateTime.now());
        
        boolean isAllNegative = "NEGATIVE".equalsIgnoreCase(test.getHivResult()) &&
                "NEGATIVE".equalsIgnoreCase(test.getHepatitisBResult()) &&
                "NEGATIVE".equalsIgnoreCase(test.getHepatitisCResult()) &&
                "NEGATIVE".equalsIgnoreCase(test.getSyphilisResult()) &&
                "NEGATIVE".equalsIgnoreCase(test.getMalariaResult());

        if (isAllNegative) {
            test.setOverallResult("PASSED");
            unit.setStatus("AVAILABLE");
            unit.setStorageLocation("Main Refrigerator A1");
        } else {
            test.setOverallResult("FAILED");
            unit.setStatus("DISCARDED");
            unit.setStorageLocation("Hazardous Medical Waste");
        }

        unitRepository.save(unit);
        BloodTest savedTest = testRepository.save(test);

        // Log transaction
        BloodInventoryTransaction tx = new BloodInventoryTransaction();
        tx.setBloodUnitId(unit.getId());
        tx.setTransactionType(isAllNegative ? "STOCK_IN" : "DISCARD");
        tx.setQuantity(1);
        tx.setReferenceId("TEST-" + savedTest.getId());
        tx.setPerformedBy(test.getTestedBy());
        tx.setNotes(isAllNegative ? "Passed lab testing, available for issue" : "Failed infectious disease lab screening");
        transactionRepository.save(tx);

        return savedTest;
    }

    // Blood Units & Inventory
    public List<BloodUnit> getAllBloodUnits() {
        return unitRepository.findAll();
    }

    public List<BloodUnit> getAvailableUnits() {
        return unitRepository.findByStatus("AVAILABLE");
    }

    // Blood Request Management
    public List<BloodRequestDetail> getAllRequests() {
        return requestRepository.findAll();
    }

    @Transactional
    public BloodRequestDetail createRequest(BloodRequestDetail request) {
        validateRequest(request);
        long reqCount = nextNumber(requestRepository.findTopByOrderByIdDesc().map(BloodRequestDetail::getId));
        request.setRequestNumber(String.format("REQ-%d-%05d", LocalDateTime.now().getYear(), reqCount));
        request.setRequestDate(LocalDateTime.now());
        request.setStatus("PENDING");
        return requestRepository.save(request);
    }

    @Transactional
    public BloodRequestDetail updateRequestStatus(Long requestId, String status) {
        BloodRequestDetail req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));
        req.setStatus(status);
        return requestRepository.save(req);
    }

    // Edit a request that has not been fulfilled yet
    @Transactional
    public BloodRequestDetail updateRequest(Long requestId, BloodRequestDetail changes) {
        BloodRequestDetail req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));
        if (!"PENDING".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be edited. This request is " + req.getStatus() + ".");
        }
        validateRequest(changes);
        req.setPatientName(changes.getPatientName().trim());
        req.setBloodType(changes.getBloodType());
        req.setQuantityRequested(changes.getQuantityRequested());
        if (changes.getUrgency() != null) req.setUrgency(changes.getUrgency());
        if (changes.getRequestedBy() != null) req.setRequestedBy(changes.getRequestedBy());
        req.setReason(changes.getReason());
        return requestRepository.save(req);
    }

    @Transactional
    public void deleteRequest(Long requestId) {
        BloodRequestDetail req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found with ID: " + requestId));
        if ("FULFILLED".equalsIgnoreCase(req.getStatus())) {
            throw new IllegalStateException("A fulfilled request has an issued blood unit and cannot be deleted.");
        }
        requestRepository.delete(req);
    }

    private void validateRequest(BloodRequestDetail request) {
        if (request.getPatientName() == null || request.getPatientName().isBlank()) {
            throw new IllegalArgumentException("Patient name is required.");
        }
        if (request.getBloodType() == null || request.getBloodType().isBlank()) {
            throw new IllegalArgumentException("Blood type is required.");
        }
        if (request.getQuantityRequested() == null || request.getQuantityRequested() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1 unit.");
        }
    }

    // Blood Issue Management
    public List<BloodIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Transactional
    public synchronized BloodIssue issueBloodUnit(Long requestId, Long bloodUnitId, String issuedBy, String notes) {
        BloodRequestDetail request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Blood request not found"));

        if ("FULFILLED".equalsIgnoreCase(request.getStatus())) {
            throw new RuntimeException("This request has already been fulfilled.");
        }

        BloodUnit unit = unitRepository.findById(bloodUnitId)
                .orElseThrow(() -> new RuntimeException("Blood unit not found"));

        if (!"AVAILABLE".equalsIgnoreCase(unit.getStatus())) {
            throw new RuntimeException("Blood unit " + unit.getUnitNumber() + " cannot be issued because status is " + unit.getStatus());
        }

        if (unit.getExpiryDate().isBefore(LocalDateTime.now())) {
            unit.setStatus("EXPIRED");
            unitRepository.save(unit);
            throw new RuntimeException("Blood unit " + unit.getUnitNumber() + " cannot be issued because it is expired.");
        }

        // Perform stock reduction / status update
        unit.setStatus("ISSUED");
        unitRepository.save(unit);

        // Create issue record
        long issueCount = nextNumber(issueRepository.findTopByOrderByIdDesc().map(BloodIssue::getId));
        BloodIssue issue = new BloodIssue();
        issue.setIssueNumber(String.format("ISS-%d-%05d", LocalDateTime.now().getYear(), issueCount));
        issue.setRequestId(request.getId());
        issue.setPatientId(request.getPatientId());
        issue.setIssuedBy(issuedBy != null ? issuedBy : "Pharmacist Staff");
        issue.setIssueDate(LocalDateTime.now());
        issue.setBloodType(unit.getBloodType());
        issue.setComponentType(request.getComponentType());
        issue.setQuantity(1);
        issue.setNotes(notes);
        issue.setStatus("COMPLETED");

        BloodIssue savedIssue = issueRepository.save(issue);

        // Update Request Status
        request.setStatus("FULFILLED");
        requestRepository.save(request);

        // Create transaction history
        BloodInventoryTransaction tx = new BloodInventoryTransaction();
        tx.setBloodUnitId(unit.getId());
        tx.setTransactionType("ISSUE");
        tx.setQuantity(1);
        tx.setReferenceId(savedIssue.getIssueNumber());
        tx.setPerformedBy(savedIssue.getIssuedBy());
        tx.setNotes("Issued to request " + request.getRequestNumber() + " for patient " + request.getPatientName());
        transactionRepository.save(tx);

        return savedIssue;
    }

    // Transactions History
    public List<BloodInventoryTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
