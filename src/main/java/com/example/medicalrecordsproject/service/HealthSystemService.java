package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemResponse;

import java.util.List;

public interface HealthSystemService {
    HealthSystemResponse createHealthSystem(HealthSystemRequest healthSystemRequest);

    List<HealthSystemResponse> getAllHealthSystems();

    HealthSystemResponse getHealthSystemById(Long id);

    HealthSystemResponse updateHealthSystem(Long id, HealthSystemRequest healthSystemRequest);

    void deleteHealthSystem(Long id);
}
