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
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Spy
    private MapperUtil mapperUtil;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient insuredPatient1;
    private Patient insuredPatient2;
    private Patient uninsuredPatient1;
    private PatientRequest patientRequestDoctorGp;
    private PatientRequest patientRequestDoctorNotGp;
    private Doctor doctorNotGp;
    private Doctor doctorIsGp;

    @BeforeEach
    public void setup() {
        insuredPatient1 = Helpers.getInsuredPatient1();
        ReflectionTestUtils.setField(insuredPatient1, "id", 1L);
        insuredPatient2 = Helpers.getInsuredPatient2();
        ReflectionTestUtils.setField(insuredPatient2, "id", 2L);

        doctorIsGp = Helpers.getDoctorIsGp();
        ReflectionTestUtils.setField(doctorIsGp, "id", 1L);
        doctorNotGp = Helpers.getDoctorNotGp();
        ReflectionTestUtils.setField(doctorNotGp, "id", 2L);

        patientRequestDoctorGp = Helpers.getPatientRequest();

        patientRequestDoctorNotGp = Helpers.getPatientRequest();
    }

    @Test
    void testCreatePatient_withPatientRequestExistingDoctorGp_shouldAddNewPatient() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(patientRepository.save(any())).willReturn(insuredPatient2);

        ReflectionTestUtils.setField(insuredPatient2, "gp", doctorIsGp);
        PatientResponse expectedPatientResponse =
                mapperUtil.modelMapper().map(insuredPatient2, PatientResponse.class);

        ReflectionTestUtils.setField(patientRequestDoctorGp, "gpId", doctorIsGp.getId());
        PatientResponse actualPatientResponse = patientService.createPatient(patientRequestDoctorGp);

        assertEquals(expectedPatientResponse, actualPatientResponse);
    }

    @Test
    void testCreatePatient_withPatientRequestExistingDoctorNotGp_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorNotGp));
        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", doctorNotGp.getId());

        assertThrows(DoctorNotGpException.class,
                () -> patientService.createPatient(patientRequestDoctorNotGp));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testCreatePatient_withPatientRequestNotExistingDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());
        ReflectionTestUtils.setField(patientRequestDoctorGp, "gpId", doctorIsGp.getId());

        assertThrows(EntityNotFoundException.class,
                () -> patientService.createPatient(patientRequestDoctorGp));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testMapPatientRequestToPatient_withPatientRequestExistingDoctorGp_shouldMap() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));

        Patient patient = new Patient();
        ReflectionTestUtils.setField(patientRequestDoctorGp, "gpId", doctorIsGp.getId());

        patientService.mapPatientRequestToPatient(patientRequestDoctorGp, patient);

        assertEquals(patient.getName(), patientRequestDoctorGp.getName());
        assertEquals(patient.isHasInsurance(), patientRequestDoctorGp.isHasInsurance());
        assertEquals(patient.getGp().getId(), patientRequestDoctorGp.getGpId());
    }

    @Test
    void testMapPatientRequestToPatient_withPatientRequestExistingDoctorNotGp_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorNotGp));
        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", doctorNotGp.getId());

        Patient patient = new Patient();

        assertThrows(DoctorNotGpException.class,
                () -> patientService.mapPatientRequestToPatient(patientRequestDoctorNotGp, patient));
    }

    @Test
    void testMapPatientRequestToPatient_withPatientRequestNotExistingDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());
        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", 5L);

        Patient patient = new Patient();

        assertThrows(EntityNotFoundException.class,
                () -> patientService.mapPatientRequestToPatient(patientRequestDoctorNotGp, patient));
    }

    @Test
    void testGetDoctorFromDoctorId_withPatientRequestExistingDoctorGp_shouldReturnDoctor() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        ReflectionTestUtils.setField(patientRequestDoctorGp, "gpId", doctorIsGp.getId());

        Doctor actual = patientService.getDoctorFromDoctorId(patientRequestDoctorGp);

        assertEquals(doctorIsGp.getId(), actual.getId());
        assertEquals(doctorIsGp.getName(), actual.getName());
        assertEquals(doctorIsGp.isGp(), actual.isGp());
    }

    @Test
    void testGetDoctorFromDoctorId_withPatientRequestExistingDoctorNotGp_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorNotGp));
        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", doctorNotGp.getId());

        assertThrows(DoctorNotGpException.class,
                () -> patientService.getDoctorFromDoctorId(patientRequestDoctorNotGp));
    }

    @Test
    void testGetDoctorFromDoctorId_withPatientRequestNotExistingDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());
        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", 5L);

        assertThrows(EntityNotFoundException.class,
                () -> patientService.getDoctorFromDoctorId(patientRequestDoctorNotGp));
    }

    @Test
    void testGetAllPatients_withPatients_shouldReturnAllPatients() {
        List<PatientResponse> expected = mapperUtil.mapList(List.of(insuredPatient1, insuredPatient2), PatientResponse.class);

        given(mapperUtil.mapList(patientRepository.findAll(), PatientResponse.class)).willReturn(expected);

        assertIterableEquals(expected, patientService.getAllPatients());
    }

    @Test
    void testGetAllPatients_withNoPatients_shouldReturnEmptyList() {
        given(mapperUtil.mapList(patientRepository.findAll(), PatientResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), patientService.getAllPatients());
    }

    @Test
    void testGetPatientById_withPatient_shouldReturnPatient() {
        PatientResponse expected = mapperUtil.modelMapper().map(insuredPatient1, PatientResponse.class);

        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));

        PatientResponse actual = patientService.getPatientById(insuredPatient1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetPatientById_withNoSuchPatient_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> patientService.getPatientById(1L));
    }

    @Test
    void testUpdatePatient_withExistingPatientExistingDoctorGp_shouldUpdatePatient() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorIsGp));
        given(patientRepository.save(any())).willReturn(insuredPatient1);

        Patient expectedUpdated = new Patient();
        ReflectionTestUtils.setField(expectedUpdated, "id", 1L);
        ReflectionTestUtils.setField(expectedUpdated, "name", "insuredPatient2");
        ReflectionTestUtils.setField(expectedUpdated, "hasInsurance", false);
        ReflectionTestUtils.setField(expectedUpdated, "gp", doctorIsGp);

        PatientResponse expectedPatientResponse =
                mapperUtil.modelMapper().map(expectedUpdated, PatientResponse.class);

        ReflectionTestUtils.setField(patientRequestDoctorGp, "gpId", doctorIsGp.getId());

        PatientResponse actualPatientResponse = patientService.updatePatient(insuredPatient1.getId(), patientRequestDoctorGp);

        assertEquals(expectedPatientResponse, actualPatientResponse);
    }

    @Test
    void testUpdatePatient_withExistingPatientExistingDoctorNotGp_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctorNotGp));

        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", doctorNotGp.getId());

        assertThrows(DoctorNotGpException.class,
                () -> patientService.updatePatient(insuredPatient1.getId(), patientRequestDoctorNotGp));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testUpdatePatient_withExistingPatientNotExistingDoctor_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());

        ReflectionTestUtils.setField(patientRequestDoctorNotGp, "gpId", doctorNotGp.getId());

        assertThrows(EntityNotFoundException.class,
                () -> patientService.updatePatient(insuredPatient1.getId(), patientRequestDoctorNotGp));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testUpdatePatient_withNotExistingPatient_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> patientService.updatePatient(insuredPatient1.getId(), patientRequestDoctorNotGp));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void testDeletePatient_withValidId_shouldDeletePatient() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.of(insuredPatient1));

        patientService.deletePatient(insuredPatient1.getId());

        verify(patientRepository, times(1)).delete(insuredPatient1);
    }

    @Test
    void testDeletePatient_withNoSuchPatient_shouldThrow() {
        given(patientRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> patientService.deletePatient(insuredPatient1.getId()));
        verify(patientRepository, never()).delete(any());
    }

    @Test
    void testGetPatientsWithInsurance_withPatientsWithInsurance_shouldReturnPatients() {
        List<PatientResponse> expected = mapperUtil.mapList(List.of(insuredPatient1, insuredPatient2), PatientResponse.class);
        given(mapperUtil.mapList(patientRepository.findAllByHasInsuranceIsTrue(), PatientResponse.class)).willReturn(expected);

        assertIterableEquals(expected, patientService.getPatientsWithInsurance());
    }

    @Test
    void testGetPatientsWithInsurance_withoutPatientsWithInsurance_shouldReturnEmptyList() {
        given(mapperUtil.mapList(patientRepository.findAllByHasInsuranceIsTrue(), PatientResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), patientService.getPatientsWithInsurance());
    }

    @Test
    void testGetPatientsWithoutInsurance_withPatientsWithoutInsurance_shouldReturnPercent() {
        uninsuredPatient1 = Helpers.getUninsuredPatient1();
        ReflectionTestUtils.setField(insuredPatient2, "id", 3L);
        List<Patient> allPatients = List.of(insuredPatient1, insuredPatient2, uninsuredPatient1);
        BigDecimal countAllPatients = BigDecimal.valueOf(allPatients.size());
        given(patientRepository.findAll()).willReturn(allPatients);
        int countUninsuredPatients = List.of(uninsuredPatient1).size();
        given(patientRepository.countAllByHasInsuranceIsFalse()).willReturn(countUninsuredPatients);

        BigDecimal expected = BigDecimal.valueOf(countUninsuredPatients)
                .divide(countAllPatients, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        assertEquals(expected, patientService.getPatientsWithoutInsurance());
    }

    @Test
    void testGetPatientsWithoutInsurance_withoutPatientsWithoutInsurance_shouldReturnPercentZero() {
        List<Patient> allPatients = List.of(insuredPatient1, insuredPatient2);
        BigDecimal countAllPatients = BigDecimal.valueOf(allPatients.size());
        given(patientRepository.findAll()).willReturn(allPatients);
        int countUninsuredPatients = 0;
        given(patientRepository.countAllByHasInsuranceIsFalse()).willReturn(countUninsuredPatients);

        BigDecimal expected = BigDecimal.valueOf(countUninsuredPatients)
                .divide(countAllPatients, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        assertEquals(expected, patientService.getPatientsWithoutInsurance());
    }

    @Test
    void testGetPatientsWithoutInsurance_withoutAnyPatients_shouldThrow() {
        given(patientRepository.findAll()).willReturn(Collections.emptyList());
        int countUninsuredPatients = 0;
        given(patientRepository.countAllByHasInsuranceIsFalse()).willReturn(countUninsuredPatients);

        assertThrows(NoPatientsException.class,
                () -> patientService.getPatientsWithoutInsurance());
    }
}