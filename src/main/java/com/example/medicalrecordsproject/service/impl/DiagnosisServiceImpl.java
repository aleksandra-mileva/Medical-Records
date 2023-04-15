package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;
import com.example.medicalrecordsproject.data.entities.Diagnosis;
import com.example.medicalrecordsproject.data.repositories.DiagnosisRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.DiagnosisService;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final MapperUtil mapperUtil;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository, MapperUtil mapperUtil) {
        this.diagnosisRepository = diagnosisRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public DiagnosisResponse createDiagnosis(DiagnosisRequest diagnosisRequest) {
        return mapperUtil.modelMapper()
                .map(diagnosisRepository
                        .save(mapperUtil.modelMapper()
                                .map(diagnosisRequest, Diagnosis.class)), DiagnosisResponse.class);
    }

    @Override
    public List<DiagnosisResponse> getAllDiagnoses() {
        return mapperUtil.mapList(diagnosisRepository.findAll(), DiagnosisResponse.class);
    }

    @Override
    public DiagnosisResponse getDiagnosisById(Long id) {
        return mapperUtil.modelMapper()
                .map(diagnosisRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"))
                        , DiagnosisResponse.class);
    }

    @Override
    public DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest diagnosisRequest) {
        Diagnosis diagnosis = this.diagnosisRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        mapperUtil.modelMapper().map(diagnosisRequest, diagnosis);

        return mapperUtil.modelMapper()
                .map(diagnosisRepository.save(diagnosis), DiagnosisResponse.class);
    }

    @Override
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = this.diagnosisRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        diagnosisRepository.delete(diagnosis);
    }
}
