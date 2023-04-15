package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemResponse;
import com.example.medicalrecordsproject.service.HealthSystemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/healthSystem")
public class HealthSystemController {

    private final HealthSystemService healthSystemService;

    public HealthSystemController(HealthSystemService healthSystemService) {
        this.healthSystemService = healthSystemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthSystemResponse createHealthSystem(@RequestBody @Valid HealthSystemRequest healthSystemRequest) {
        return healthSystemService.createHealthSystem(healthSystemRequest);
    }

    @GetMapping
    public List<HealthSystemResponse> retrieveAll() {
        return healthSystemService.getAllHealthSystems();
    }

    @GetMapping("/{id}")
    public HealthSystemResponse retrieveSingle(@PathVariable Long id) {
        return healthSystemService.getHealthSystemById(id);
    }

    @PutMapping("/{id}")
    public HealthSystemResponse updateHealthSystem(@PathVariable Long id, @RequestBody @Valid HealthSystemRequest healthSystemRequest) {
        return healthSystemService.updateHealthSystem(id, healthSystemRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHealthSystem(@PathVariable Long id) {
        healthSystemService.deleteHealthSystem(id);
    }
}
