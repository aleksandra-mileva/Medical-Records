package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;
import com.example.medicalrecordsproject.service.DiagnosisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/diagnoses")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisResponse createDiagnosis(@RequestBody @Valid DiagnosisRequest diagnosisRequest) {
        return diagnosisService.createDiagnosis(diagnosisRequest);
    }

    @GetMapping
    public List<DiagnosisResponse> retrieveAll() {
        return diagnosisService.getAllDiagnoses();
    }

    @GetMapping("/{id}")
    public DiagnosisResponse retrieveSingle(@PathVariable Long id) {
        return diagnosisService.getDiagnosisById(id);
    }

    @PutMapping("/{id}")
    public DiagnosisResponse updateDiagnosis(@PathVariable Long id, @RequestBody @Valid DiagnosisRequest diagnosisRequest) {
        return diagnosisService.updateDiagnosis(id, diagnosisRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
    }
}
