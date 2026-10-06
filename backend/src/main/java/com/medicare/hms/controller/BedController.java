package com.medicare.hms.controller;

import com.medicare.hms.model.Bed;
import com.medicare.hms.service.BedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beds")
@CrossOrigin(origins = "*")
public class BedController {

    private final BedService bedService;

    public BedController(BedService bedService) {
        this.bedService = bedService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Bed> addBed(@RequestBody Bed bed) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bedService.addBed(bed));
    }

    // READ - all beds, or only one ward's beds with ?wardId=WD-ICU
    @GetMapping
    public ResponseEntity<List<Bed>> getAllBeds(@RequestParam(required = false) String wardId) {
        return ResponseEntity.ok(wardId == null ? bedService.getAllBeds() : bedService.getBedsByWard(wardId));
    }

    // READ - one bed
    @GetMapping("/{id}")
    public ResponseEntity<Bed> getBedById(@PathVariable String id) {
        return bedService.getBedById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Bed> updateBed(@PathVariable String id, @RequestBody Bed bed) {
        return ResponseEntity.ok(bedService.updateBed(id, bed));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBed(@PathVariable String id) {
        bedService.deleteBed(id);
        return ResponseEntity.ok(Map.of("message", "Bed " + id + " deleted."));
    }
}
