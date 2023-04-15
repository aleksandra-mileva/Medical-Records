package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.visits.VisitRequest;
import com.example.medicalrecordsproject.data.dtos.visits.VisitResponse;
import com.example.medicalrecordsproject.data.entities.*;
import com.example.medicalrecordsproject.data.repositories.*;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.exceptions.NegativeIncomeException;
import com.example.medicalrecordsproject.helpers.Helpers;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceImplTest {

    @Mock
    private VisitRepository visitRepository;

    @Spy
    private MapperUtil mapperUtil;

    @Mock(lenient = true)
    private PatientRepository patientRepository;

    @Mock(lenient = true)
    private DoctorRepository doctorRepository;

    @Mock(lenient = true)
    private DiagnosisRepository diagnosisRepository;

    @Mock(lenient = true)
    private HealthSystemRepository healthSystemRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit visitContains1;
    private Visit visitContains2;
    private Visit visitNotContains;
    private VisitRequest visitCreateRequest;
    private VisitRequest visitUpdateRequest;
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
        ReflectionTestUtils.setField(doctorNotGp, "id", 1L);
        doctorIsGp = Helpers.getDoctorIsGp();
        ReflectionTestUtils.setField(doctorIsGp, "id", 2L);
        healthSystem = Helpers.getHealthSystem();
        ReflectionTestUtils.setField(healthSystem, "id", 1L);
        insuredPatient1 = Helpers.getInsuredPatient1();
        ReflectionTestUtils.setField(insuredPatient1, "id", 1L);
        uninsuredPatient1 = Helpers.getUninsuredPatient1();
        ReflectionTestUtils.setField(uninsuredPatient1, "id", 2L);
        diagnosis1 = Helpers.getDiagnosis1();
        ReflectionTestUtils.setField(diagnosis1, "id", 1L);
        diagnosis2 = Helpers.getDiagnosis2();
        ReflectionTestUtils.setField(diagnosis2, "id", 2L);

        visitContains1 = new Visit();
        ReflectionTestUtils.setField(visitContains1, "id", 1L);
        ReflectionTestUtils.setField(visitContains1, "patient", insuredPatient1);
        ReflectionTestUtils.setField(visitContains1, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitContains1, "doctor", doctorIsGp);
        ReflectionTestUtils.setField(visitContains1, "diagnoses", Set.of(diagnosis1));
        ReflectionTestUtils.setField(visitContains1, "visitDate", LocalDate.of(2021, 8, 11));

        visitContains2 = new Visit();
        ReflectionTestUtils.setField(visitContains2, "id", 2L);
        ReflectionTestUtils.setField(visitContains2, "patient", insuredPatient1);
        ReflectionTestUtils.setField(visitContains2, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitContains2, "doctor", doctorIsGp);
        ReflectionTestUtils.setField(visitContains2, "diagnoses", Set.of(diagnosis1, diagnosis2));
        ReflectionTestUtils.setField(visitContains2, "visitDate", LocalDate.of(2007, 7, 21));

        visitNotContains = new Visit();
        ReflectionTestUtils.setField(visitNotContains, "id", 3L);
        ReflectionTestUtils.setField(visitNotContains, "patient", uninsuredPatient1);
        ReflectionTestUtils.setField(visitNotContains, "doctor", doctorNotGp);
        ReflectionTestUtils.setField(visitNotContains, "diagnoses", Set.of(diagnosis2));
        ReflectionTestUtils.setField(visitNotContains, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(visitNotContains, "visitDate", LocalDate.of(2011, 7, 21));

        visitCreateRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitCreateRequest, "patientId", insuredPatient1.getId());
        ReflectionTestUtils.setField(visitCreateRequest, "healthSystemId", healthSystem.getId());
        ReflectionTestUtils.setField(visitCreateRequest, "doctorId", doctorIsGp.getId());
        ReflectionTestUtils.setField(visitCreateRequest, "diagnosesIds", Set.of(diagnosis1.getId(), diagnosis2.getId()));
        ReflectionTestUtils.setField(visitCreateRequest, "visitDate", LocalDate.of(2021, 8, 11));

        visitUpdateRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitUpdateRequest, "patientId", uninsuredPatient1.getId());
        ReflectionTestUtils.setField(visitUpdateRequest, "healthSystemId", healthSystem.getId());
        ReflectionTestUtils.setField(visitUpdateRequest, "doctorId", doctorNotGp.getId());
        ReflectionTestUtils.setField(visitUpdateRequest, "diagnosesIds", Set.of(diagnosis1.getId()));
        ReflectionTestUtils.setField(visitUpdateRequest, "visitDate", LocalDate.of(1996, 11, 7));
    }

    @Test
    void testCreateVisit_withAllPropertiesExisting_shouldAddNewVisit() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));
        given(visitRepository.save(any())).willReturn(visitContains1);

        VisitResponse expectedVisitResponse =
                mapperUtil.modelMapper().map(visitContains1, VisitResponse.class);

        VisitResponse actualVisitResponse = visitService.createVisit(visitCreateRequest);

        assertEquals(expectedVisitResponse, actualVisitResponse);
    }

    @Test
    void testCreateVisit_withOneDiagnosisNotExisting_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.empty());
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        assertThrows(EntityNotFoundException.class,
                () -> visitService.createVisit(visitCreateRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testCreateVisit_withPatientNotExisting_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.empty());
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        assertThrows(EntityNotFoundException.class,
                () -> visitService.createVisit(visitCreateRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testCreateVisit_withDoctorNotExisting_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        assertThrows(EntityNotFoundException.class,
                () -> visitService.createVisit(visitCreateRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testCreateVisit_withHealthSystemNotExisting_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> visitService.createVisit(visitCreateRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testMapVisitRequestToVisit_withVisitRequestAllPropertiesExisting_shouldMap() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        Visit visit = new Visit();

        visitService.mapVisitRequestToVisit(visitCreateRequest, visit);

        assertEquals(visit.getPatient().getId(), visitCreateRequest.getPatientId());
        assertEquals(visit.getDoctor().getId(), visitCreateRequest.getDoctorId());
        assertEquals(visit.getDiagnoses().size(), visitCreateRequest.getDiagnosesIds().size());
        assertEquals(visit.getHealthSystem().getId(), visitCreateRequest.getHealthSystemId());
        assertEquals(visit.getVisitDate(), visitCreateRequest.getVisitDate());
    }

    @Test
    void testMapVisitRequestToVisit_withOneDiagnosisNotExisting_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.empty());
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        Visit visit = new Visit();

        assertThrows(EntityNotFoundException.class,
                () -> visitService.mapVisitRequestToVisit(visitCreateRequest, visit));
    }

    @Test
    void testGetDiagnosesFromDiagnosesIds_withVisitRequestExistingDiagnoses_shouldReturnDiagnoses() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.of(diagnosis2));

        Set<Diagnosis> expected = Set.of(diagnosis1, diagnosis2);

        Set<Diagnosis> actual = visitService.getDiagnosesFromDiagnosesIds(visitCreateRequest);

        assertEquals(expected.size(), actual.size());
        for (Diagnosis diagnosis : expected) {
            assertTrue(actual.contains(diagnosis));
        }
    }

    @Test
    void testGetDiagnosesFromDiagnosesIds_withVisitRequestOneNotExistingDiagnoses_shouldThrow() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(diagnosisRepository.findById(diagnosis2.getId())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> visitService.getDiagnosesFromDiagnosesIds(visitCreateRequest));
    }

    @Test
    void testGetAllVisits_withVisits_shouldReturnAllVisits() {
        List<VisitResponse> expected = mapperUtil.mapList(List.of(visitContains1, visitContains2), VisitResponse.class);

        given(mapperUtil.mapList(visitRepository.findAll(), VisitResponse.class)).willReturn(expected);

        assertIterableEquals(expected, visitService.getAllVisits());
    }

    @Test
    void testGetAllVisits_withNoVisits_shouldReturnEmptyList() {
        given(mapperUtil.mapList(doctorRepository.findAll(), VisitResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), visitService.getAllVisits());
    }

    @Test
    void testGetVisitById_withVisit_shouldReturnVisit() {
        VisitResponse expected = mapperUtil.modelMapper().map(visitContains1, VisitResponse.class);

        given(visitRepository.findById(anyLong())).willReturn(Optional.of(visitContains1));

        VisitResponse actual = visitService.getVisitById(visitContains1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetVisitById_withNoSuchVisit_shouldThrow() {
        given(visitRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> visitService.getVisitById(1L));
    }

    @Test
    void testUpdateVisit_withExistingVisitAllPropertiesExisting_shouldUpdateVisit() {
        given(visitRepository.findById(visitContains1.getId())).willReturn(Optional.of(visitContains1));
        given(patientRepository.findById(uninsuredPatient1.getId())).willReturn(Optional.of(uninsuredPatient1));
        given(doctorRepository.findById(doctorNotGp.getId())).willReturn(Optional.of(doctorNotGp));
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(healthSystemRepository.findById(healthSystem.getId())).willReturn(Optional.of(healthSystem));
        given(visitRepository.save(any())).willReturn(visitContains1);

        Visit expectedUpdated = new Visit();
        ReflectionTestUtils.setField(expectedUpdated, "id", 1L);
        ReflectionTestUtils.setField(expectedUpdated, "patient", uninsuredPatient1);
        ReflectionTestUtils.setField(expectedUpdated, "healthSystem", healthSystem);
        ReflectionTestUtils.setField(expectedUpdated, "doctor", doctorNotGp);
        ReflectionTestUtils.setField(expectedUpdated, "diagnoses", Set.of(diagnosis1));
        ReflectionTestUtils.setField(expectedUpdated, "visitDate", LocalDate.of(1996, 11, 7));

        VisitResponse expectedVisitResponse =
                mapperUtil.modelMapper().map(expectedUpdated, VisitResponse.class);

        VisitResponse actualVisitResponse = visitService.updateVisit(visitContains1.getId(), visitUpdateRequest);

        assertEquals(expectedVisitResponse, actualVisitResponse);
    }

    @Test
    void testUpdateVisit_withExistingVisitOneNotExistingProperty_shouldThrow() {
        given(visitRepository.findById(visitContains1.getId())).willReturn(Optional.of(visitContains1));
        given(patientRepository.findById(uninsuredPatient1.getId())).willReturn(Optional.of(uninsuredPatient1));
        given(doctorRepository.findById(doctorNotGp.getId())).willReturn(Optional.of(doctorNotGp));
        given(healthSystemRepository.findById(healthSystem.getId())).willReturn(Optional.of(healthSystem));

        assertThrows(EntityNotFoundException.class,
                () -> visitService.updateVisit(visitContains1.getId(), visitUpdateRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testDeleteVisit_withValidId_shouldDeleteVisit() {
        given(visitRepository.findById(anyLong())).willReturn(Optional.of(visitContains1));

        visitService.deleteVisit(visitContains1.getId());

        verify(visitRepository, times(1)).delete(visitContains1);
    }

    @Test
    void testDeleteVisit_withNoSuchVisit_shouldThrow() {
        given(visitRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> visitService.deleteVisit(visitContains1.getId()));
        verify(visitRepository, never()).save(any());
    }

    @Test
    void testGetTotalIncomeFromVisits_withVisits_shouldReturnIncome() {
        List<Visit> allVisits = List.of(visitContains1, visitContains2, visitNotContains);
        given(visitRepository.findAll()).willReturn(allVisits);

        BigDecimal expected = allVisits.stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(expected, visitService.getTotalIncomeFromVisits());
    }

    @Test
    void testGetTotalIncomeFromVisits_withoutVisits_shouldReturnZero() {
        given(visitRepository.findAll()).willReturn(Collections.emptyList());

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, visitService.getTotalIncomeFromVisits());
    }

    @Test
    void testGetTotalIncomeFromVisitsOfDoctor_withExistingDoctorHasVisits_shouldReturnIncome() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));

        List<Visit> allVisitsByDoctor = List.of(visitContains1, visitContains2);
        given(visitRepository.findAllByDoctorId(doctorIsGp.getId())).willReturn(allVisitsByDoctor);

        BigDecimal expected = allVisitsByDoctor.stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(expected, visitService.getTotalIncomeFromVisitsOfDoctor(doctorIsGp.getId()));
    }

    @Test
    void testGetTotalIncomeFromVisitsOfDoctor_withExistingDoctorHasNoVisits_shouldReturnZero() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(visitRepository.findAllByDoctorId(doctorIsGp.getId())).willReturn(Collections.emptyList());

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, visitService.getTotalIncomeFromVisitsOfDoctor(doctorIsGp.getId()));
    }

    @Test
    void testGetTotalIncomeFromVisitsOfDoctor_NotExistingDoctor_shouldThrow() {

        assertThrows(EntityNotFoundException.class,
                () -> visitService.getTotalIncomeFromVisitsOfDoctor(doctorIsGp.getId()));
    }

    @Test
    void testGetCountVisitsOfPatient_withExistingPatientHasVisits_shouldReturnCount() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));

        int expectedVisitsByPatient = List.of(visitContains1, visitContains2).size();
        given(visitRepository.countAllByPatientId(insuredPatient1.getId())).willReturn(expectedVisitsByPatient);


        assertEquals(expectedVisitsByPatient, visitService.getCountVisitsOfPatient(insuredPatient1.getId()));
    }

    @Test
    void testGetCountVisitsOfPatient_withExistingPatientHasNoVisits_shouldReturnZero() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(visitRepository.countAllByPatientId(insuredPatient1.getId())).willReturn(0);

        assertEquals(0, visitService.getCountVisitsOfPatient(insuredPatient1.getId()));
    }

    @Test
    void testGetCountVisitsOfPatient_NotExistingPatient_shouldThrow() {
        assertThrows(EntityNotFoundException.class,
                () -> visitService.getCountVisitsOfPatient(insuredPatient1.getId()));
    }

    @Test
    void testGetCountVisitsByDiagnosis_withExistingDiagnosisHasVisits_shouldReturnCount() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));

        int expectedVisitsByDiagnosis = List.of(visitContains1, visitContains2).size();
        given(visitRepository.countAllByDiagnosisId(diagnosis1.getId())).willReturn(expectedVisitsByDiagnosis);

        assertEquals(expectedVisitsByDiagnosis, visitService.getCountVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetCountVisitsByDiagnosis_withExistingDiagnosisHasNoVisits_shouldReturnZero() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(visitRepository.countAllByDiagnosisId(diagnosis1.getId())).willReturn(0);

        assertEquals(0, visitService.getCountVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetCountVisitsByDiagnosis_NotExistingDiagnosis_shouldThrow() {
        assertThrows(EntityNotFoundException.class,
                () -> visitService.getCountVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetCountDoctorsBiggerIncome_withDoctorsWithBiggerIncome_shouldReturnCount() {
        Object[] row = new Object[2];
        row[0] = doctorIsGp;
        row[1] = BigDecimal.valueOf(20);
        List<Object[]> expectedRows = new ArrayList<>();
        expectedRows.add(row);

        int expected = expectedRows.size();

        BigDecimal minIncome = BigDecimal.valueOf(15);
        given(visitRepository.getAllDoctorsBiggerIncome(minIncome)).willReturn(expectedRows);

        assertEquals(expected, visitService.getCountDoctorsBiggerIncome(minIncome));
    }

    @Test
    void testGetCountDoctorsBiggerIncome_withoutDoctorsWithBiggerIncome_shouldReturnZero() {
        int expected = 0;

        BigDecimal minIncome = BigDecimal.valueOf(15);
        given(visitRepository.getAllDoctorsBiggerIncome(minIncome)).willReturn(Collections.emptyList());

        assertEquals(expected, visitService.getCountDoctorsBiggerIncome(minIncome));
    }

    @Test
    void testGetCountDoctorsBiggerIncome_negativeMinIncome_shouldThrow() {
        BigDecimal minIncome = BigDecimal.valueOf(-15);

        assertThrows(NegativeIncomeException.class,
                () -> visitService.getCountDoctorsBiggerIncome(minIncome));
    }

    @Test
    void testGetTotalIncomeFromVisitsByDiagnosis_withExistingDiagnosisHasVisits_shouldReturnIncome() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));

        List<Visit> allVisitsByDiagnosis = List.of(visitContains1, visitContains2);
        given(visitRepository.findAllVisitsByDiagnosis(diagnosis1.getId())).willReturn(allVisitsByDiagnosis);

        BigDecimal expected = allVisitsByDiagnosis.stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(expected, visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetTotalIncomeFromVisitsByDiagnosis_withExistingDiagnosisHasNoVisits_shouldReturnZero() {
        given(diagnosisRepository.findById(diagnosis1.getId())).willReturn(Optional.of(diagnosis1));
        given(visitRepository.findAllVisitsByDiagnosis(diagnosis1.getId())).willReturn(Collections.emptyList());

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetTotalIncomeFromVisitsByDiagnosis_NotExistingDiagnosis_shouldThrow() {

        assertThrows(EntityNotFoundException.class,
                () -> visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosis1.getId()));
    }

    @Test
    void testGetTotalIncomeFromPatientsNoInsurance_withVisits_shouldReturnIncome() {
        List<Visit> allVisitsUninsuredPatients = List.of(visitNotContains);
        given(visitRepository.findAllByPatient_HasInsuranceFalse()).willReturn(allVisitsUninsuredPatients);

        BigDecimal expected = allVisitsUninsuredPatients.stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(expected, visitService.getTotalIncomeFromPatientsNoInsurance());
    }

    @Test
    void testGetTotalIncomeFromPatientsNoInsurance_withoutVisits_shouldReturnZero() {
        given(visitRepository.findAllByPatient_HasInsuranceFalse()).willReturn(Collections.emptyList());

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, visitService.getTotalIncomeFromPatientsNoInsurance());
    }

    @Test
    void testGetTotalIncomeByDoctorInsuredPatients_withExistingDoctorHasVisitsWithInsuredPatients_shouldReturnIncome() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));

        List<Visit> visitsByDoctorWithInsuredPatients = List.of(visitContains1, visitContains2);
        given(visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorIsGp.getId()))
                .willReturn(visitsByDoctorWithInsuredPatients);

        BigDecimal expected = visitsByDoctorWithInsuredPatients.stream()
                .map(visit -> visit.getHealthSystem().getNoInsuranceFee())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(expected, visitService.getTotalIncomeByDoctorInsuredPatients(doctorIsGp.getId()));
    }

    @Test
    void testGetTotalIncomeByDoctorInsuredPatients_withExistingDoctorHasNoVisitsWithInsuredPatients_shouldReturnZero() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorNotGp));
        given(visitRepository.findAllByDoctorIdAndPatient_HasInsuranceTrue(doctorNotGp.getId()))
                .willReturn(Collections.emptyList());

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, visitService.getTotalIncomeByDoctorInsuredPatients(doctorNotGp.getId()));
    }

    @Test
    void testGetTotalIncomeByDoctorInsuredPatients_NotExistingDoctor_shouldThrow() {
        assertThrows(EntityNotFoundException.class,
                () -> visitService.getTotalIncomeByDoctorInsuredPatients(doctorIsGp.getId()));
    }
}