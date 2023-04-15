package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponse createDiagnosis(DiagnosisRequest diagnosisRequest);

    List<DiagnosisResponse> getAllDiagnoses();

    DiagnosisResponse getDiagnosisById(Long id);

    DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest diagnosisRequest);

    void deleteDiagnosis(Long id);
}
