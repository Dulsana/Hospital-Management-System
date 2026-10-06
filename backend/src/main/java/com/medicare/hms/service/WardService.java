package com.medicare.hms.service;

import com.medicare.hms.model.Ward;
import com.medicare.hms.repository.BedRepository;
import com.medicare.hms.repository.WardRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WardService {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;

    public WardService(WardRepository wardRepository, BedRepository bedRepository) {
        this.wardRepository = wardRepository;
        this.bedRepository = bedRepository;
    }

    // Create
    @Transactional
    public Ward addWard(Ward ward) {
        String id = cleanId(ward.getWardId());
        validate(ward);
        // save() would silently overwrite an existing ward with the same ID, so check first
        if (wardRepository.existsById(id)) {
            throw new IllegalStateException("Ward ID " + id + " already exists.");
        }
        if (wardRepository.existsByWardNumber(ward.getWardNumber())) {
            throw new IllegalStateException("Ward number " + ward.getWardNumber() + " is already used.");
        }
        ward.setWardId(id);
        ward.setWardType(ward.getWardType().trim());
        return wardRepository.save(ward);
    }

    // Read - all wards, in ward-number order
    public List<Ward> getAllWards() {
        return wardRepository.findAll(Sort.by("wardNumber"));
    }

    // Read - one ward
    public Optional<Ward> getWardById(String id) {
        return wardRepository.findById(id);
    }

    // Update (the ward ID itself cannot change)
    @Transactional
    public Ward updateWard(String id, Ward ward) {
        Ward existingWard = wardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ward not found: " + id));
        validate(ward);
        if (wardRepository.existsByWardNumberAndWardIdNot(ward.getWardNumber(), id)) {
            throw new IllegalStateException("Ward number " + ward.getWardNumber() + " is already used.");
        }
        existingWard.setWardNumber(ward.getWardNumber());
        existingWard.setWardType(ward.getWardType().trim());
        return wardRepository.save(existingWard);
    }

    // Delete - only when no beds are left in the ward
    @Transactional
    public void deleteWard(String id) {
        if (!wardRepository.existsById(id)) {
            throw new IllegalArgumentException("Ward not found: " + id);
        }
        long beds = bedRepository.countByWardId(id);
        if (beds > 0) {
            throw new IllegalStateException("Cannot delete this ward because it has " + beds
                    + " bed(s). Delete or move the beds first.");
        }
        wardRepository.deleteById(id);
    }

    private void validate(Ward ward) {
        if (ward.getWardNumber() == null || ward.getWardNumber() <= 0) {
            throw new IllegalArgumentException("Ward number must be greater than 0.");
        }
        if (ward.getWardType() == null || ward.getWardType().isBlank()) {
            throw new IllegalArgumentException("Ward type is required.");
        }
    }

    static String cleanId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID is required.");
        }
        String clean = id.trim();
        if (clean.length() > 30 || clean.contains(" ")) {
            throw new IllegalArgumentException("ID must be at most 30 characters with no spaces, e.g. WD-ICU or BD-01.");
        }
        return clean;
    }
}
