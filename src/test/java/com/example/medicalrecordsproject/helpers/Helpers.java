package com.example.medicalrecordsproject.helpers;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.doctors.DoctorRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.patients.PatientRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.entities.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

public class Helpers {

    public static Patient getInsuredPatient1() {
        Patient patient = new Patient();
        ReflectionTestUtils.setField(patient, "name", "insuredPatient1");
        ReflectionTestUtils.setField(patient, "hasInsurance", true);

        return patient;
    }

    public static PatientRequest getPatientRequest() {
        PatientRequest patientRequest = new PatientRequest();
        ReflectionTestUtils.setField(patientRequest, "name", "insuredPatient2");
        ReflectionTestUtils.setField(patientRequest, "hasInsurance", false);

        return patientRequest;
    }

    public static Patient getInsuredPatient2() {
        Patient patient = new Patient();
        ReflectionTestUtils.setField(patient, "name", "insuredPatient2");
        ReflectionTestUtils.setField(patient, "hasInsurance", true);

        return patient;
    }

    public static Patient getUninsuredPatient1() {
        Patient patient = new Patient();
        ReflectionTestUtils.setField(patient, "name", "uninsuredPatient1");
        ReflectionTestUtils.setField(patient, "hasInsurance", false);

        return patient;
    }

    public static Patient getUninsuredPatient2() {
        Patient patient = new Patient();
        ReflectionTestUtils.setField(patient, "name", "uninsuredPatient2");
        ReflectionTestUtils.setField(patient, "hasInsurance", false);

        return patient;
    }

    public static Diagnosis getDiagnosis1() {
        Diagnosis diagnosis = new Diagnosis();
        ReflectionTestUtils.setField(diagnosis, "name", "covid");

        return diagnosis;
    }

    public static Diagnosis getDiagnosis2() {
        Diagnosis diagnosis = new Diagnosis();
        ReflectionTestUtils.setField(diagnosis, "name", "virus");

        return diagnosis;
    }

    public static DiagnosisRequest getDiagnosisRequest() {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        ReflectionTestUtils.setField(diagnosisRequest, "name", "covid");

        return diagnosisRequest;
    }

    public static Specialty getSpecialty1() {
        Specialty specialty = new Specialty();
        ReflectionTestUtils.setField(specialty, "name", "cardiologist");

        return specialty;
    }

    public static Specialty getSpecialty2() {
        Specialty specialty = new Specialty();
        ReflectionTestUtils.setField(specialty, "name", "neurologist");

        return specialty;
    }

    public static SpecialtyRequest getSpecialtyRequest() {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        ReflectionTestUtils.setField(specialtyRequest, "name", "cardiologist");

        return specialtyRequest;
    }

    public static Doctor getDoctorIsGp() {
        Doctor doctor = new Doctor();
        ReflectionTestUtils.setField(doctor, "name", "Meridith Grey");
        ReflectionTestUtils.setField(doctor, "isGp", true);

        return doctor;
    }

    public static Doctor getDoctorNotGp() {
        Doctor doctor = new Doctor();
        ReflectionTestUtils.setField(doctor, "name", "Cristina Yang");
        ReflectionTestUtils.setField(doctor, "isGp", false);

        return doctor;
    }

    public static DoctorRequest getDoctorRequest() {
        DoctorRequest doctorRequest = new DoctorRequest();
        ReflectionTestUtils.setField(doctorRequest, "name", "New Name");
        ReflectionTestUtils.setField(doctorRequest, "isGp", false);

        return doctorRequest;
    }

    public static HealthSystem getHealthSystem() {
        HealthSystem healthSystem = new HealthSystem();
        ReflectionTestUtils.setField(healthSystem, "noInsuranceFee", BigDecimal.valueOf(10));

        return healthSystem;
    }

    public static HealthSystemRequest getHealthSystemRequest() {
        HealthSystemRequest healthSystemRequest = new HealthSystemRequest();
        ReflectionTestUtils.setField(healthSystemRequest, "noInsuranceFee", BigDecimal.valueOf(20));

        return healthSystemRequest;
    }
}
