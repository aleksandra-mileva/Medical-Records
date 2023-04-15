package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {

    SpecialtyResponse createSpecialty(SpecialtyRequest specialtyRequest);

    List<SpecialtyResponse> getAllSpecialties();

    SpecialtyResponse getSpecialtyById(Long id);

    SpecialtyResponse updateSpecialty(Long id, SpecialtyRequest specialtyRequest);

    void deleteSpecialty(Long id);
}
