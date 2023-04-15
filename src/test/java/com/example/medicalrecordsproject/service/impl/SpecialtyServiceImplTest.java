package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.example.medicalrecordsproject.data.entities.Specialty;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Spy
    private MapperUtil mapperUtil;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty specialty1;
    private Specialty specialty2;
    private SpecialtyRequest specialtyRequest;

    @BeforeEach
    public void setup() {
        specialty1 = Helpers.getSpecialty1();
        ReflectionTestUtils.setField(specialty1, "id", 1L);

        specialty2 = Helpers.getSpecialty2();
        ReflectionTestUtils.setField(specialty2, "id", 2L);

        specialtyRequest = Helpers.getSpecialtyRequest();
    }

    @Test
    void testCreateSpecialty_withSpecialtyRequest_shouldAddNewSpecialty() {
        given(specialtyRepository.save(any())).willReturn(specialty1);

        SpecialtyResponse expectedSpecialtyResponse =
                mapperUtil.modelMapper().map(specialty1, SpecialtyResponse.class);

        SpecialtyResponse actualSpecialtyResponse = specialtyService.createSpecialty(specialtyRequest);

        assertEquals(expectedSpecialtyResponse, actualSpecialtyResponse);
    }

    @Test
    void testGetAllSpecialties_withSpecialties_shouldReturnAllSpecialties() {
        List<SpecialtyResponse> expected = mapperUtil.mapList(List.of(specialty1, specialty2), SpecialtyResponse.class);

        given(mapperUtil.mapList(specialtyRepository.findAll(), SpecialtyResponse.class)).willReturn(expected);

        assertIterableEquals(expected, specialtyService.getAllSpecialties());
    }

    @Test
    void testGetAllSpecialties_withNoSpecialties_shouldReturnEmptyList() {
        given(mapperUtil.mapList(specialtyRepository.findAll(), SpecialtyResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), specialtyService.getAllSpecialties());
    }

    @Test
    void testGetSpecialtyById_withSpecialty_shouldReturnSpecialty() {
        SpecialtyResponse expected = mapperUtil.modelMapper().map(specialty1, SpecialtyResponse.class);

        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty1));

        SpecialtyResponse actual = specialtyService.getSpecialtyById(specialty1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetSpecialtyById_withNoSuchSpecialty_shouldThrow() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> specialtyService.getSpecialtyById(1L));
    }

    @Test
    void testUpdateSpecialty_withSpecialty_shouldUpdateSpecialty() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty2));

        Specialty expectedUpdated = new Specialty();
        ReflectionTestUtils.setField(expectedUpdated, "id", 2L);
        ReflectionTestUtils.setField(expectedUpdated, "name", "cardiologist");

        SpecialtyResponse expectedSpecialtyResponse =
                mapperUtil.modelMapper().map(expectedUpdated, SpecialtyResponse.class);

        given(specialtyRepository.save(any())).willReturn(specialty2);

        SpecialtyResponse actualSpecialtyResponse = specialtyService
                .updateSpecialty(specialty2.getId(), specialtyRequest);

        assertEquals(expectedSpecialtyResponse, actualSpecialtyResponse);
    }

    @Test
    void testUpdateSpecialty_withNoSuchSpecialty_shouldThrow() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> specialtyService.updateSpecialty(specialty2.getId(), specialtyRequest));
        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void testDeleteSpecialty_withValidId_shouldDeleteSpecialty() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty1));

        specialtyService.deleteSpecialty(specialty1.getId());

        verify(specialtyRepository, times(1)).delete(specialty1);
    }

    @Test
    void testDeleteSpecialty_withNoSuchSpecialty_shouldThrow() {
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> specialtyService.deleteSpecialty(specialty1.getId()));
        verify(specialtyRepository, never()).delete(any());
    }
}