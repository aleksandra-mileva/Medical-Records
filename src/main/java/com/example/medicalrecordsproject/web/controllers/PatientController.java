package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.patients.PatientRequest;
import com.example.medicalrecordsproject.data.dtos.patients.PatientResponse;
import com.example.medicalrecordsproject.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse createPatient(@RequestBody @Valid PatientRequest patientRequest) {
        return patientService.createPatient(patientRequest);
    }

    @GetMapping
    public List<PatientResponse> retrieveAll() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientResponse retrieveSingle(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    public PatientResponse updatePatient(@PathVariable Long id, @RequestBody @Valid PatientRequest patientRequest) {
        return patientService.updatePatient(id, patientRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/has-insurance")
    public List<PatientResponse> getPatientsWithInsurance() {
        return patientService.getPatientsWithInsurance();
    }

    @GetMapping("/percent-without-insurance")
    public BigDecimal getPatientsWithoutInsurance() {
        return patientService.getPatientsWithoutInsurance();
    }
}
