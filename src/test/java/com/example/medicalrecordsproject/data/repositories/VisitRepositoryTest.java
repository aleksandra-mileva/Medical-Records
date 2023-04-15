package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.*;
import com.example.medicalrecordsproject.helpers.Helpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VisitRepositoryTest {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Visit visitContains1;
    private Visit visitContains2;
    private Visit visitNotContains;
    private Doctor doctorNotGp;
    private Doctor doctorIsGp;
    private Patient insuredPatient1;
    private Patient uninsuredPatient1;
    private Diagnosis diagnosis1;
    private Diagnosis diagnosis2;
    HealthSystem healthSystem;

    @BeforeEach
    public void setup() {
        doctorNotGp = Helpers.getDoctorNotGp();
        testEntityManager.persistAndFlush(doctorNotGp);

        doctorIsGp = Helpers.getDoctorIsGp();
        testEntityManager.persistAndFlush(doctorIsGp);

        healthSystem = Helpers.getHealthSystem();
        testEntityManager.persistAndFlush(healthSystem);

        insuredPatient1 = Helpers.getInsuredPatient1();
        testEntityManager.persistAndFlush(insuredPatient1);

        uninsuredPatient1 = Helpers.getUninsuredPatient1();
        testEntityManager.persistAndFlush(uninsuredPatient1);

        diagnosis1 = Helpers.getDiagnosis1();
        testEntityManager.persistAndFlush(diagnosis1);

        diagnosis2 = Helpers.getDiagnosis2();
        testEntityManager.persistAndFlush(diagnosis2);

        visitContains1 = new Visit();
        ReflectionTestUtils.setField(visitContains1, "patient", insuredPatient1);
        ReflectionTestUtils.setField(visitContains1, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitContains1, "doctor", doctorIsGp);
        ReflectionTestUtils.setField(visitContains1, "diagnoses", Set.of(diagnosis1));
        ReflectionTestUtils.setField(visitContains1, "visitDate", LocalDate.of(2021, 8, 11));

        visitContains2 = new Visit();
        ReflectionTestUtils.setField(visitContains2, "patient", insuredPatient1);
        ReflectionTestUtils.setField(visitContains2, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitContains2, "doctor", doctorIsGp);
        ReflectionTestUtils.setField(visitContains2, "diagnoses", Set.of(diagnosis1, diagnosis2));
        ReflectionTestUtils.setField(visitContains2, "visitDate", LocalDate.of(2007, 7, 21));

        visitNotContains = new Visit();
        ReflectionTestUtils.setField(visitNotContains, "patient", uninsuredPatient1);
        ReflectionTestUtils.setField(visitNotContains, "doctor", doctorNotGp);
        ReflectionTestUtils.setField(visitNotContains, "diagnoses", Set.of(diagnosis2));
        ReflectionTestUtils.setField(visitNotContains, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitNotContains, "visitDate", LocalDate.of(2011, 7, 21));
    }

    @Test()
    void testFindAllByDoctorId_withVisitsWithDoctorId_shouldReturnVisits() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        List<Visit> expected = List.of(visitContains1, visitContains2);

        assertIterableEquals(expected, visitRepository.findAllByDoctorId(doctorIsGp.getId()));
    }

    @Test()
    void testFindAllByDoctorId_withVisitsWithoutDoctorId_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllByDoctorId(doctorNotGp.getId()));
    }

    @Test()
    void testFindAllByDoctorId_notExistingDoctor_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        long notExistId = 5L;

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllByDoctorId(notExistId));
    }

    @Test()
    void testCountAllByPatientId_withVisitsWithPatientId_shouldReturnCount() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        int expected = List.of(visitContains1, visitContains2).size();

        assertEquals(expected, visitRepository.countAllByPatientId(insuredPatient1.getId()));
    }

    @Test()
    void testCountAllByPatientId_withVisitsWithoutPatientId_shouldReturnZero() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);

        int expected = 0;

        assertEquals(expected, visitRepository.countAllByPatientId(uninsuredPatient1.getId()));
    }

    @Test()
    void testCountAllByPatientId_notExistingPatient_shouldReturnZero() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);

        long notExistId = 5L;
        int expected = 0;

        assertEquals(expected, visitRepository.countAllByPatientId(notExistId));
    }

    @Test()
    void testCountAllByDiagnosisId_withVisitsWithDiagnosisId_shouldReturnCount() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        int expected = List.of(visitContains1, visitContains2).size();

        assertEquals(expected, visitRepository.countAllByDiagnosisId(diagnosis1.getId()));
    }

    @Test()
    void testCountAllByDiagnosisId_withVisitsWithoutDiagnosisId_shouldReturnZero() {

        testEntityManager.persistAndFlush(visitContains1);

        int expected = 0;

        assertEquals(expected, visitRepository.countAllByDiagnosisId(diagnosis2.getId()));
    }

    @Test()
    void testCountAllByDiagnosisId_notExistingDiagnosis_shouldReturnZero() {

        testEntityManager.persistAndFlush(visitContains1);

        long notExistId = 5L;
        int expected = 0;

        assertEquals(expected, visitRepository.countAllByDiagnosisId(notExistId));
    }

    @Test()
    void testGetAllDoctorsBiggerIncome_withVisitsWithDoctorsBiggerIncome_shouldReturnCollection() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        int expected = 1;
        BigDecimal minIncome = BigDecimal.valueOf(15);

        assertEquals(expected, visitRepository.getAllDoctorsBiggerIncome(minIncome).size());
    }

    @Test()
    void testGetAllDoctorsBiggerIncome_withVisitsWithDoctorsBiggerIncome_shouldReturnEmptyCollection() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        int expected = 0;
        BigDecimal minIncome = BigDecimal.valueOf(30);

        assertEquals(expected, visitRepository.getAllDoctorsBiggerIncome(minIncome).size());
    }

    @Test()
    void testFindAllVisitsByDiagnosis_withVisitsWithDiagnosis_shouldReturnVisits() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        List<Visit> expected = List.of(visitContains1, visitContains2);

        assertIterableEquals(expected, visitRepository.findAllVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test()
    void testFindAllVisitsByDiagnosis_withVisitsWithoutDiagnosis_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllVisitsByDiagnosis(diagnosis2.getId()));
    }

    @Test()
    void testFindAllVisitsByDiagnosis_notExistingDiagnosis_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        long notExistId = 5L;

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllVisitsByDiagnosis(notExistId));
    }

    @Test()
    void testFindAllByPatient_HasInsuranceFalse_withVisitsWithUninsuredPatients_shouldReturnVisits() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        List<Visit> expected = List.of(visitNotContains);

        assertIterableEquals(expected, visitRepository.findAllByPatient_HasInsuranceFalse());
    }

    @Test()
    void testFindAllByPatient_HasInsuranceFalse_withVisitsWithoutUninsuredPatients_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllByPatient_HasInsuranceFalse());
    }

    @Test()
    void testFindAllByPatient_HasInsuranceFalse_withoutVisits_shouldReturnEmptyList() {

        assertIterableEquals(Collections.emptyList(), visitRepository.findAllByPatient_HasInsuranceFalse());
    }

    @Test()
    void testFindAllByDoctorIdAndPatient_HasInsuranceTrue_withExistingDoctorInsuredPatient_shouldReturnVisits() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        List<Visit> expected = List.of(visitContains1, visitContains2);

        assertIterableEquals(expected, visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorIsGp.getId()));
    }

    @Test()
    void testFindAllByDoctorIdAndPatient_HasInsuranceTrue_withExistingDoctorUninsuredPatient_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        assertIterableEquals(Collections.emptyList(),
                visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorNotGp.getId()));
    }

    @Test()
    void testFindAllByDoctorIdAndPatient_HasInsuranceTrue_notExistingDoctor_shouldReturnEmptyList() {

        testEntityManager.persistAndFlush(visitContains1);
        testEntityManager.persistAndFlush(visitContains2);
        testEntityManager.persistAndFlush(visitNotContains);

        long notExistId = 5L;

        assertIterableEquals(Collections.emptyList(),
                visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(notExistId));
    }

    @Test()
    void testFindAllByDoctorIdAndPatient_HasInsuranceTrue_noVisits_shouldReturnEmptyList() {

        assertIterableEquals(Collections.emptyList(),
                visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorIsGp.getId()));
    }
}