package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.patients.PatientRequest;
import com.example.medicalrecordsproject.data.dtos.patients.PatientResponse;

import java.math.BigDecimal;
import java.util.List;

public interface PatientService {

    List<PatientResponse> getAllPatients();

    PatientResponse getPatientById(Long id);

    PatientResponse createPatient(PatientRequest patientRequest);

    PatientResponse updatePatient(Long id, PatientRequest patientRequest);

    void deletePatient(Long id);

    List<PatientResponse> getPatientsWithInsurance();

    BigDecimal getPatientsWithoutInsurance();
}
