package com.medicare.hms.controller;

import com.medicare.hms.model.OperationTheatre;
import com.medicare.hms.repository.OperationTheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ot")
@CrossOrigin(origins = "*")
public class OperationTheatreController {

    private final OperationTheatreRepository otRepository;

    @Autowired
    public OperationTheatreController(OperationTheatreRepository otRepository) {
        this.otRepository = otRepository;
    }

    @GetMapping
    public ResponseEntity<List<OperationTheatre>> getOTSchedules() {
        return ResponseEntity.ok(otRepository.findAll());
    }
}
