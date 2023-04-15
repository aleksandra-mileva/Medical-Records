package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemResponse;
import com.example.medicalrecordsproject.data.entities.HealthSystem;
import com.example.medicalrecordsproject.data.repositories.HealthSystemRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.HealthSystemService;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthSystemServiceImpl implements HealthSystemService {

    private final HealthSystemRepository healthSystemRepository;
    private final MapperUtil mapperUtil;

    public HealthSystemServiceImpl(HealthSystemRepository healthSystemRepository, MapperUtil mapperUtil) {
        this.healthSystemRepository = healthSystemRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public HealthSystemResponse createHealthSystem(HealthSystemRequest healthSystemRequest) {
        return mapperUtil.modelMapper()
                .map(healthSystemRepository
                        .save(mapperUtil.modelMapper()
                                .map(healthSystemRequest, HealthSystem.class)), HealthSystemResponse.class);
    }

    @Override
    public List<HealthSystemResponse> getAllHealthSystems() {
        return mapperUtil.mapList(healthSystemRepository.findAll(), HealthSystemResponse.class);
    }

    @Override
    public HealthSystemResponse getHealthSystemById(Long id) {
        return mapperUtil.modelMapper()
                .map(healthSystemRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Health System not found"))
                        , HealthSystemResponse.class);
    }

    @Override
    public HealthSystemResponse updateHealthSystem(Long id, HealthSystemRequest healthSystemRequest) {
        HealthSystem healthSystem = this.healthSystemRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Health System not found"));

        mapperUtil.modelMapper().map(healthSystemRequest, healthSystem);

        return mapperUtil.modelMapper().map(healthSystemRepository.save(healthSystem), HealthSystemResponse.class);
    }

    @Override
    public void deleteHealthSystem(Long id) {
        HealthSystem healthSystem = this.healthSystemRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Health System not found"));

        healthSystemRepository.delete(healthSystem);
    }
}
