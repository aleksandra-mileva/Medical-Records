package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.visits.VisitRequest;
import com.example.medicalrecordsproject.data.dtos.visits.VisitResponse;
import com.example.medicalrecordsproject.data.entities.*;
import com.example.medicalrecordsproject.data.repositories.*;
import com.example.medicalrecordsproject.exceptions.*;
import com.example.medicalrecordsproject.service.*;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    private final MapperUtil mapperUtil;

    private final PatientRepository patientRepository;

    private final DoctorRepository doctorRepository;

    private final DiagnosisRepository diagnosisRepository;

    private final HealthSystemRepository healthSystemRepository;

    public VisitServiceImpl(VisitRepository visitRepository, MapperUtil mapperUtil,
                            PatientRepository patientRepository, DoctorRepository doctorRepository,
                            DiagnosisRepository diagnosisRepository, HealthSystemRepository healthSystemRepository) {
        this.visitRepository = visitRepository;
        this.mapperUtil = mapperUtil;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.healthSystemRepository = healthSystemRepository;
    }

    @Override
    public VisitResponse createVisit(VisitRequest visitRequest) {

        Visit visit = new Visit();
        mapVisitRequestToVisit(visitRequest, visit);

        return mapperUtil.modelMapper().map(visitRepository.save(visit), VisitResponse.class);
    }

    public void mapVisitRequestToVisit(VisitRequest visitRequest, Visit visit) {
        // If one of the given Diagnoses doesn't exist -> We don't create the Visit because Exception is thrown.
        Set<Diagnosis> diagnosesFromDiagnosesIds = getDiagnosesFromDiagnosesIds(visitRequest);

        visit.setPatient(patientRepository.findById(visitRequest.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found")));
        visit.setDoctor(doctorRepository.findById(visitRequest.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found")));
        visit.setHealthSystem(healthSystemRepository.findById(visitRequest.getHealthSystemId())
                .orElseThrow(() -> new EntityNotFoundException("Health System not found")));
        visit.setVisitDate(visitRequest.getVisitDate());
        visit.setDiagnoses(diagnosesFromDiagnosesIds);
    }

    public Set<Diagnosis> getDiagnosesFromDiagnosesIds(VisitRequest visitRequest) {
        Set<Diagnosis> diagnoses = new HashSet<>();

        for (Long diagnosesId : visitRequest.getDiagnosesIds()) {
            diagnoses.add(diagnosisRepository.findById(diagnosesId)
                    .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found")));
        }

        return diagnoses;
    }

    @Override
    public List<VisitResponse> getAllVisits() {
        return mapperUtil.mapList(visitRepository.findAll(), VisitResponse.class);
    }

    @Override
    public VisitResponse getVisitById(Long id) {
        return mapperUtil.modelMapper()
                .map(visitRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Visit not found"))
                        , VisitResponse.class);
    }

    @Override
    public VisitResponse updateVisit(Long id, VisitRequest visitRequest) {
        Visit visit = this.visitRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        mapVisitRequestToVisit(visitRequest, visit);

        return mapperUtil.modelMapper().map(visitRepository.save(visit), VisitResponse.class);
    }

    @Override
    public void deleteVisit(Long id) {
        Visit visit = this.visitRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Visit not found"));

        visitRepository.delete(visit);
    }

    @Override
    public BigDecimal getTotalIncomeFromVisits() {
        return visitRepository.findAll().stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalIncomeFromVisitsOfDoctor(Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        return visitRepository.findAllByDoctorId(doctorId).stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int getCountVisitsOfPatient(Long patientId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        return visitRepository.countAllByPatientId(patientId);
    }

    @Override
    public int getCountVisitsByDiagnosis(Long diagnosisId) {
        diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        return visitRepository.countAllByDiagnosisId(diagnosisId);
    }

    @Override
    public Integer getCountDoctorsBiggerIncome(BigDecimal minIncome) {
        // method with native Query:
//        return visitRepository.getCountDoctorsBiggerIncome(minIncome);
        if (minIncome.compareTo(BigDecimal.ZERO) < 0){
            throw new NegativeIncomeException("The minimal income cannot be negative");
        }

        List<Object[]> rows = visitRepository.getAllDoctorsBiggerIncome(minIncome);
        return rows.size();
    }

    @Override
    public BigDecimal getTotalIncomeFromVisitsByDiagnosis(Long diagnosisId) {
        // If the given Diagnosis doesn't exist -> Exception
        diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new EntityNotFoundException("Diagnosis not found"));

        return visitRepository.findAllVisitsByDiagnosis(diagnosisId).stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalIncomeFromPatientsNoInsurance() {
        return visitRepository.findAllByPatient_HasInsuranceFalse().stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalIncomeByDoctorInsuredPatients(Long doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        return visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorId).stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
