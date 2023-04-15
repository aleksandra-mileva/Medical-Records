package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.visits.VisitRequest;
import com.example.medicalrecordsproject.data.dtos.visits.VisitResponse;

import java.math.BigDecimal;
import java.util.List;

public interface VisitService {
    List<VisitResponse> getAllVisits();

    VisitResponse createVisit(VisitRequest visitRequest);

    VisitResponse getVisitById(Long id);

    VisitResponse updateVisit(Long id, VisitRequest visitRequest);

    void deleteVisit(Long id);

    BigDecimal getTotalIncomeFromVisits();

    BigDecimal getTotalIncomeFromVisitsOfDoctor(Long doctorId);

    int getCountVisitsOfPatient(Long patientId);

    int getCountVisitsByDiagnosis(Long diagnosisId);

    Integer getCountDoctorsBiggerIncome(BigDecimal minIncome);

    BigDecimal getTotalIncomeFromVisitsByDiagnosis(Long diagnosisId);

    BigDecimal getTotalIncomeFromPatientsNoInsurance();

    BigDecimal getTotalIncomeByDoctorInsuredPatients(Long doctorId);
}
