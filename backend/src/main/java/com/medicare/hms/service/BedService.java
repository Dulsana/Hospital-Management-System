package com.medicare.hms.service;

import com.medicare.hms.model.Bed;
import com.medicare.hms.repository.BedRepository;
import com.medicare.hms.repository.WardRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BedService {

    public static final List<String> STATUSES = List.of("Available", "Occupied", "Reserved", "Maintenance");

    private final BedRepository bedRepository;
    private final WardRepository wardRepository;

    public BedService(BedRepository bedRepository, WardRepository wardRepository) {
        this.bedRepository = bedRepository;
        this.wardRepository = wardRepository;
    }

    // Create
    @Transactional
    public Bed addBed(Bed bed) {
        String id = WardService.cleanId(bed.getBedId());
        validate(bed);
        // save() would silently overwrite an existing bed with the same ID, so check first
        if (bedRepository.existsById(id)) {
            throw new IllegalStateException("Bed ID " + id + " already exists.");
        }
        if (bedRepository.existsByWardIdAndBedNumber(bed.getWardId(), bed.getBedNumber())) {
            throw new IllegalStateException("Bed number " + bed.getBedNumber() + " already exists in ward " + bed.getWardId() + ".");
        }
        bed.setBedId(id);
        return bedRepository.save(bed);
    }

    // Read all, grouped by ward then bed number
    public List<Bed> getAllBeds() {
        return bedRepository.findAll(Sort.by("wardId", "bedNumber"));
    }

    // Read one
    public Optional<Bed> getBedById(String id) {
        return bedRepository.findById(id);
    }

    // Read the beds of one ward
    public List<Bed> getBedsByWard(String wardId) {
        return bedRepository.findByWardId(wardId);
    }

    // Update (the bed ID itself cannot change; the bed can move to another ward)
    @Transactional
    public Bed updateBed(String id, Bed bed) {
        Bed existingBed = bedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bed not found: " + id));
        validate(bed);
        if (bedRepository.existsByWardIdAndBedNumberAndBedIdNot(bed.getWardId(), bed.getBedNumber(), id)) {
            throw new IllegalStateException("Bed number " + bed.getBedNumber() + " already exists in ward " + bed.getWardId() + ".");
        }
        existingBed.setBedNumber(bed.getBedNumber());
        existingBed.setStatus(bed.getStatus());
        existingBed.setWardId(bed.getWardId());
        existingBed.setPatientName(bed.getPatientName());
        return bedRepository.save(existingBed);
    }

    // Delete - an occupied bed must be freed first
    @Transactional
    public void deleteBed(String id) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bed not found: " + id));
        if ("Occupied".equals(bed.getStatus())) {
            throw new IllegalStateException("Bed " + id + " is occupied. Discharge the patient (set it to Available) before deleting it.");
        }
        bedRepository.delete(bed);
    }

    // Checks the bed and tidies its values (status spelling, patient name)
    private void validate(Bed bed) {
        if (bed.getBedNumber() == null || bed.getBedNumber() <= 0) {
            throw new IllegalArgumentException("Bed number must be greater than 0.");
        }
        String status = STATUSES.stream()
                .filter(s -> s.equalsIgnoreCase(bed.getStatus() == null ? "" : bed.getStatus().trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status must be one of " + STATUSES + "."));
        bed.setStatus(status);
        if (bed.getWardId() == null || !wardRepository.existsById(bed.getWardId())) {
            throw new IllegalArgumentException("Please select a valid ward.");
        }
        // A free bed or one under maintenance has no patient
        if ("Available".equals(status) || "Maintenance".equals(status)
                || bed.getPatientName() == null || bed.getPatientName().isBlank()) {
            bed.setPatientName(null);
        } else {
            bed.setPatientName(bed.getPatientName().trim());
        }
    }
}
