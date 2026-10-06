package com.medicare.hms.controller;

import com.medicare.hms.model.OperationTheatre;
import com.medicare.hms.repository.OperationTheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Tell Spring: "This class handles web/API requests and returns JSON data"
@RestController

//The web address for this code is: http://localhost:8080/api/ot
@RequestMapping("/api/ot")

//Allow websites/frontend apps (like React, Angular) to talk to this backend
@CrossOrigin(origins = "*")
public class OperationTheatreController {

    // Connection to the database tool
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
