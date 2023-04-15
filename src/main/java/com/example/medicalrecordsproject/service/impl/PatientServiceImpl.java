package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.patients.PatientRequest;
import com.example.medicalrecordsproject.data.dtos.patients.PatientResponse;
import com.example.medicalrecordsproject.data.entities.Doctor;
import com.example.medicalrecordsproject.data.entities.Patient;
import com.example.medicalrecordsproject.data.repositories.DoctorRepository;
import com.example.medicalrecordsproject.data.repositories.PatientRepository;
import com.example.medicalrecordsproject.exceptions.DoctorNotGpException;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.exceptions.NoPatientsException;
import com.example.medicalrecordsproject.service.PatientService;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final MapperUtil mapperUtil;
    private final DoctorRepository doctorRepository;

    public PatientServiceImpl(PatientRepository patientRepository, MapperUtil mapperUtil, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.mapperUtil = mapperUtil;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public PatientResponse createPatient(PatientRequest patientRequest) {

        Patient patient = new Patient();
        mapPatientRequestToPatient(patientRequest, patient);

        return mapperUtil.modelMapper().map(patientRepository.save(patient), PatientResponse.class);
    }

    public void mapPatientRequestToPatient(PatientRequest patientRequest, Patient patient) {
        //If the given Doctor doesn't exist or if he is not GP -> We don't create the Patient because Exception is thrown
        Doctor doctorFromDoctorId = getDoctorFromDoctorId(patientRequest);

        patient.setName(patientRequest.getName());
        patient.setHasInsurance(patientRequest.isHasInsurance());
        patient.setGp(doctorFromDoctorId);
    }

    public Doctor getDoctorFromDoctorId(PatientRequest patientRequest) {
        if (patientRequest.getGpId() != null) {
            Doctor doctor = doctorRepository.findById(patientRequest.getGpId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

            if (!doctor.isGp()) {
                throw new DoctorNotGpException("Doctor with id " + patientRequest.getGpId() + " cannot be general practitioner.");
            } else {
                return doctor;
            }
        }
        return null;
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return mapperUtil.mapList(patientRepository.findAll(), PatientResponse.class);
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        return mapperUtil.modelMapper()
                .map(patientRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Patient not found"))
                        , PatientResponse.class);
    }

    @Override
    public PatientResponse updatePatient(Long id, PatientRequest patientRequest) {

        Patient patient = this.patientRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        mapPatientRequestToPatient(patientRequest, patient);

        return mapperUtil.modelMapper().map(patientRepository.save(patient), PatientResponse.class);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = this.patientRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        patientRepository.delete(patient);
    }

    @Override
    public List<PatientResponse> getPatientsWithInsurance() {
        return mapperUtil.mapList(patientRepository.findAllByHasInsuranceIsTrue(), PatientResponse.class);
    }

    @Override
    public BigDecimal getPatientsWithoutInsurance() {
        BigDecimal countAll = BigDecimal.valueOf(patientRepository.findAll().size());
        BigDecimal countWithoutInsurance = BigDecimal.valueOf(patientRepository.countAllByHasInsuranceIsFalse());

        if (countAll.equals(BigDecimal.ZERO)) {
            throw new NoPatientsException("There are no patients.");
        }

        return countWithoutInsurance.divide(countAll, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
}
