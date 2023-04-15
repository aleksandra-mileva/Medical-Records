package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.helpers.Helpers;
import com.example.medicalrecordsproject.data.entities.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Patient insuredPatient1;
    private Patient insuredPatient2;
    private Patient uninsuredPatient1;
    private Patient uninsuredPatient2;

    @BeforeEach
    public void setup() {
        this.insuredPatient1 = Helpers.getInsuredPatient1();
        this.insuredPatient2 = Helpers.getInsuredPatient2();
        this.uninsuredPatient1 = Helpers.getUninsuredPatient1();
        this.uninsuredPatient2 = Helpers.getUninsuredPatient2();
    }

    @Test()
    void testFindAllByHasInsuranceIsTrue_withInsuredPatients_shouldReturnPatients() {
        testEntityManager.persistAndFlush(insuredPatient1);
        testEntityManager.persistAndFlush(insuredPatient2);
        testEntityManager.persistAndFlush(uninsuredPatient1);

        List<Patient> expected = List.of(insuredPatient1, insuredPatient2);

        assertIterableEquals(expected, patientRepository.findAllByHasInsuranceIsTrue());
    }

    @Test()
    void testFindAllByHasInsuranceIsTrue_withoutInsuredPatients_shouldReturnEmptyList() {
        testEntityManager.persistAndFlush(uninsuredPatient1);
        testEntityManager.persistAndFlush(uninsuredPatient2);

        assertIterableEquals(Collections.emptyList(), patientRepository.findAllByHasInsuranceIsTrue());
    }

    @Test()
    void testFindAllByHasInsuranceIsTrue_withoutPatients_shouldReturnEmptyList() {

        assertIterableEquals(Collections.emptyList(), patientRepository.findAllByHasInsuranceIsTrue());
    }

    @Test()
    void testCountAllByHasInsuranceIsFalse_withUninsuredPatients_shouldReturnCount() {
        testEntityManager.persistAndFlush(uninsuredPatient1);
        testEntityManager.persistAndFlush(uninsuredPatient2);
        testEntityManager.persistAndFlush(insuredPatient1);


        int expected = List.of(uninsuredPatient1, uninsuredPatient2).size();

        assertEquals(expected, patientRepository.countAllByHasInsuranceIsFalse());
    }

    @Test()
    void testCountAllByHasInsuranceIsFalse_withoutUninsuredPatients_shouldReturnZero() {
        testEntityManager.persistAndFlush(insuredPatient1);
        testEntityManager.persistAndFlush(insuredPatient2);


        int expected = 0;

        assertEquals(expected, patientRepository.countAllByHasInsuranceIsFalse());
    }

    @Test()
    void testCountAllByHasInsuranceIsFalse_withoutPatients_shouldReturnZero() {

        int expected = 0;

        assertEquals(expected, patientRepository.countAllByHasInsuranceIsFalse());
    }
}