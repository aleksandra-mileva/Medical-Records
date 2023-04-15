package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.visits.VisitRequest;
import com.example.medicalrecordsproject.data.dtos.visits.VisitResponse;
import com.example.medicalrecordsproject.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponse createVisit(@RequestBody @Valid VisitRequest visitRequest) {
        return visitService.createVisit(visitRequest);
    }

    @GetMapping
    public List<VisitResponse> retrieveAll() {
        return visitService.getAllVisits();
    }

    @GetMapping("/{id}")
    public VisitResponse retrieveSingle(@PathVariable Long id) {
        return visitService.getVisitById(id);
    }

    @PutMapping("/{id}")
    public VisitResponse updateVisit(@PathVariable Long id, @RequestBody @Valid VisitRequest visitRequest) {
        return visitService.updateVisit(id, visitRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVisit(@PathVariable Long id) {
        visitService.deleteVisit(id);
    }

    @GetMapping("/total-income")
    public BigDecimal getTotalIncomeFromVisits() {
        return visitService.getTotalIncomeFromVisits();
    }

    @GetMapping("/total-income-by-doctor/{doctorId}")
    public BigDecimal getTotalIncomeFromVisitsOfDoctor(@PathVariable Long doctorId) {
        return visitService.getTotalIncomeFromVisitsOfDoctor(doctorId);
    }

    @GetMapping("/count-visits-by-patient/{patientId}")
    public int getCountVisitsOfPatient(@PathVariable Long patientId) {
        return visitService.getCountVisitsOfPatient(patientId);
    }

    @GetMapping("/count-visits-by-diagnosis/{diagnosisId}")
    public int getCountVisitsByDiagnosis(@PathVariable Long diagnosisId) {
        return visitService.getCountVisitsByDiagnosis(diagnosisId);
    }

    @GetMapping("/count-doctors-by-income/{minIncome}")
    public Integer getCountDoctorsBiggerIncome(@PathVariable BigDecimal minIncome) {
        return visitService.getCountDoctorsBiggerIncome(minIncome);
    }

    @GetMapping("/total-income-by-diagnosis/{diagnosisId}")
    public BigDecimal getTotalIncomeFromVisitsByDiagnosis(@PathVariable Long diagnosisId) {
        return visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosisId);
    }

    @GetMapping("/total-income-by-patients-no-insurance")
    public BigDecimal getTotalIncomeFromPatientsNoInsurance() {
        return visitService.getTotalIncomeFromPatientsNoInsurance();
    }

    @GetMapping("/total-income-by-doctor-insured-patients/{doctorId}")
    public BigDecimal getTotalIncomeByDoctorInsuredPatients(@PathVariable Long doctorId) {
        return visitService.getTotalIncomeByDoctorInsuredPatients(doctorId);
    }
}
