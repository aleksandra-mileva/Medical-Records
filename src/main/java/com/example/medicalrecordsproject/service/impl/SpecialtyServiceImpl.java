package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.example.medicalrecordsproject.data.entities.Specialty;
import com.example.medicalrecordsproject.data.repositories.SpecialtyRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.SpecialtyService;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final MapperUtil mapperUtil;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, MapperUtil mapperUtil) {
        this.specialtyRepository = specialtyRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public SpecialtyResponse createSpecialty(SpecialtyRequest specialtyRequest) {
        return mapperUtil.modelMapper()
                .map(specialtyRepository
                        .save(mapperUtil.modelMapper()
                                .map(specialtyRequest, Specialty.class)), SpecialtyResponse.class);
    }

    @Override
    public List<SpecialtyResponse> getAllSpecialties() {
        return mapperUtil.mapList(specialtyRepository.findAll(), SpecialtyResponse.class);
    }

    @Override
    public SpecialtyResponse getSpecialtyById(Long id) {
        return mapperUtil.modelMapper()
                .map(specialtyRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Specialty not found"))
                        , SpecialtyResponse.class);
    }

    @Override
    public SpecialtyResponse updateSpecialty(Long id, SpecialtyRequest specialtyRequest) {
        Specialty specialty = this.specialtyRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        mapperUtil.modelMapper().map(specialtyRequest, specialty);

        return mapperUtil.modelMapper()
                .map(specialtyRepository.save(specialty), SpecialtyResponse.class);

    }

    @Override
    public void deleteSpecialty(Long id) {
        Specialty specialty = this.specialtyRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Specialty not found"));

        specialtyRepository.delete(specialty);
    }
}
