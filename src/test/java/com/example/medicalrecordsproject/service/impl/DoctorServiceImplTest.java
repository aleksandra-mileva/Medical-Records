package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.doctors.DoctorRequest;
import com.example.medicalrecordsproject.data.dtos.doctors.DoctorResponse;
import com.example.medicalrecordsproject.data.entities.Doctor;
import com.example.medicalrecordsproject.data.entities.Specialty;
import com.example.medicalrecordsproject.data.repositories.DoctorRepository;
import com.example.medicalrecordsproject.data.repositories.SpecialtyRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock(lenient = true)
    private SpecialtyRepository specialtyRepository;

    @Spy
    private MapperUtil mapperUtil;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor1;

    private Specialty specialty1;

    private Specialty specialty2;

    private DoctorRequest doctorRequest;

    @BeforeEach
    public void setup() {
        doctor1 = Helpers.getDoctorIsGp();
        ReflectionTestUtils.setField(doctor1, "id", 1L);

        specialty1 = Helpers.getSpecialty1();
        ReflectionTestUtils.setField(specialty1, "id", 1L);

        specialty2 = Helpers.getSpecialty2();
        ReflectionTestUtils.setField(specialty2, "id", 2L);

        doctorRequest = Helpers.getDoctorRequest();
    }

    @Test
    void testCreateDoctor_withExistingSpecialties_shouldAddNewDoctor() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty1));
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty2));
        given(doctorRepository.save(any())).willReturn(doctor1);

        ReflectionTestUtils.setField(doctor1, "specialties", Set.of(specialty1, specialty2));

        DoctorResponse expectedDoctorResponse =
                mapperUtil.modelMapper().map(doctor1, DoctorResponse.class);

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));
        DoctorResponse actualDoctorResponse = doctorService.createDoctor(doctorRequest);

        assertEquals(expectedDoctorResponse, actualDoctorResponse);
    }

    @Test
    void testCreateDoctor_withOneNotExistingSpecialty_shouldThrow() {
//        given(specialtyRepository.findById(specialty1.getId())).willReturn(Optional.of(specialty1));
//        given(specialtyRepository.findById(specialty2.getId())).willReturn(Optional.empty());

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));

        assertThrows(EntityNotFoundException.class,
                () -> doctorService.createDoctor(doctorRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testCreateDoctor_withNoSpecialties_shouldAddNewDoctor() {
        given(doctorRepository.save(any())).willReturn(doctor1);

        DoctorResponse expectedDoctorResponse =
                mapperUtil.modelMapper().map(doctor1, DoctorResponse.class);

        DoctorResponse actualDoctorResponse = doctorService.createDoctor(doctorRequest);

        assertEquals(expectedDoctorResponse, actualDoctorResponse);
    }

    @Test
    void testGetSpecialtiesFromSpecialtiesIds_withDoctorRequestExistingSpecialties_shouldReturnSpecialties() {
        given(specialtyRepository.findById(specialty1.getId())).willReturn(Optional.of(specialty1));
        given(specialtyRepository.findById(specialty2.getId())).willReturn(Optional.of(specialty2));
        Set<Specialty> expected = Set.of(specialty1, specialty2);

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));

        Set<Specialty> actual = doctorService.getSpecialtiesFromSpecialtiesIds(doctorRequest);

        assertEquals(expected.size(), actual.size());
        for (Specialty specialty : expected) {
            assertTrue(actual.contains(specialty));
        }
    }

    @Test
    void testGetSpecialtiesFromSpecialtiesIds_withDoctorRequestOneNotExistingSpecialty_shouldThrow() {
        given(specialtyRepository.findById(specialty1.getId())).willReturn(Optional.of(specialty1));

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));

        assertThrows(EntityNotFoundException.class,
                () -> doctorService.getSpecialtiesFromSpecialtiesIds(doctorRequest));
    }

    @Test
    void testGetAllDoctors_withDoctors_shouldReturnAllDoctors() {
        List<DoctorResponse> expected = mapperUtil.mapList(List.of(doctor1), DoctorResponse.class);

        given(mapperUtil.mapList(doctorRepository.findAll(), DoctorResponse.class)).willReturn(expected);

        assertIterableEquals(expected, doctorService.getAllDoctors());
    }

    @Test
    void testGetAllDoctors_withNoDoctors_shouldReturnEmptyList() {
        given(mapperUtil.mapList(doctorRepository.findAll(), DoctorResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), doctorService.getAllDoctors());
    }

    @Test
    void testGetDoctorById_withDoctor_shouldReturnDoctor() {
        DoctorResponse expected = mapperUtil.modelMapper().map(doctor1, DoctorResponse.class);

        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctor1));

        DoctorResponse actual = doctorService.getDoctorById(doctor1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetDoctorById_withNoSuchDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> doctorService.getDoctorById(1L));
    }

    @Test
    void testUpdateDoctor_withExistingDoctorExistingSpecialties_shouldUpdateDoctor() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctor1));
        given(specialtyRepository.findById(specialty1.getId())).willReturn(Optional.of(specialty1));
        given(specialtyRepository.findById(specialty2.getId())).willReturn(Optional.of(specialty2));
        given(doctorRepository.save(any())).willReturn(doctor1);

        Doctor expectedUpdated = new Doctor();
        ReflectionTestUtils.setField(expectedUpdated, "id", 1L);
        ReflectionTestUtils.setField(expectedUpdated, "name", "New Name");
        ReflectionTestUtils.setField(expectedUpdated, "isGp", false);
        ReflectionTestUtils.setField(expectedUpdated, "specialties", Set.of(specialty1, specialty2));

        DoctorResponse expectedDoctorResponse =
                mapperUtil.modelMapper().map(expectedUpdated, DoctorResponse.class);

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));

        DoctorResponse actualDoctorResponse = doctorService.updateDoctor(doctor1.getId(), doctorRequest);

        assertEquals(expectedDoctorResponse, actualDoctorResponse);
    }

    @Test
    void testUpdateDoctor_withExistingDoctorOneNotExistingSpecialty_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctor1));
        given(specialtyRepository.findById(specialty1.getId())).willReturn(Optional.of(specialty1));

        ReflectionTestUtils.setField(doctorRequest, "specialtiesIds", Set.of(specialty1.getId(), specialty2.getId()));

        assertThrows(EntityNotFoundException.class,
                () -> doctorService.updateDoctor(doctor1.getId(), doctorRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testUpdateDoctor_withNotExistingDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> doctorService.updateDoctor(doctor1.getId(), doctorRequest));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testDeleteDoctor_withValidId_shouldDeleteDoctor() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.of(doctor1));

        doctorService.deleteDoctor(doctor1.getId());

        verify(doctorRepository, times(1)).delete(doctor1);
    }

    @Test
    void testDeleteDoctor_withNoSuchDoctor_shouldThrow() {
        given(doctorRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> doctorService.deleteDoctor(doctor1.getId()));
        verify(doctorRepository, never()).save(any());
    }
}