package com.medicare.hms.controller;

import com.medicare.hms.model.Ward;
import com.medicare.hms.service.WardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wards")
@CrossOrigin(origins = "*")
public class WardController {

    private final WardService wardService;

    public WardController(WardService wardService) {
        this.wardService = wardService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Ward> addWard(@RequestBody Ward ward) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wardService.addWard(ward));
    }

    // READ - all wards
    @GetMapping
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardService.getAllWards());
    }

    // READ - one ward
    @GetMapping("/{id}")
    public ResponseEntity<Ward> getWardById(@PathVariable String id) {
        return wardService.getWardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Ward> updateWard(@PathVariable String id, @RequestBody Ward ward) {
        return ResponseEntity.ok(wardService.updateWard(id, ward));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteWard(@PathVariable String id) {
        wardService.deleteWard(id);
        return ResponseEntity.ok(Map.of("message", "Ward " + id + " deleted."));
    }
}
