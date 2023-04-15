package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;
import com.example.medicalrecordsproject.data.entities.Diagnosis;
import com.example.medicalrecordsproject.data.repositories.DiagnosisRepository;
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
class DiagnosisServiceImplTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Spy
    private MapperUtil mapperUtil;

    @InjectMocks
    private DiagnosisServiceImpl diagnosisService;

    private Diagnosis diagnosis1;
    private Diagnosis diagnosis2;
    private DiagnosisRequest diagnosisRequest;

    @BeforeEach
    public void setup() {
        diagnosis1 = Helpers.getDiagnosis1();
        ReflectionTestUtils.setField(diagnosis1, "id", 1L);

        diagnosis2 = Helpers.getDiagnosis2();
        ReflectionTestUtils.setField(diagnosis2, "id", 2L);

        diagnosisRequest = Helpers.getDiagnosisRequest();
    }

    @Test
    void testCreateDiagnosis_withDiagnosisRequest_shouldAddNewDiagnosis() {
        given(diagnosisRepository.save(any())).willReturn(diagnosis1);

        DiagnosisResponse expectedDiagnosisResponse =
                mapperUtil.modelMapper().map(diagnosis1, DiagnosisResponse.class);

        DiagnosisResponse actualDiagnosisResponse = diagnosisService.createDiagnosis(diagnosisRequest);

        assertEquals(expectedDiagnosisResponse, actualDiagnosisResponse);
    }

    @Test
    void testGetAllDiagnoses_withDiagnoses_shouldReturnAllDiagnosis() {
        List<DiagnosisResponse> expected = mapperUtil.mapList(List.of(diagnosis1, diagnosis2), DiagnosisResponse.class);

        given(mapperUtil.mapList(diagnosisRepository.findAll(), DiagnosisResponse.class)).willReturn(expected);

        assertIterableEquals(expected, diagnosisService.getAllDiagnoses());
    }

    @Test
    void testGetAllDiagnoses_withNoDiagnoses_shouldReturnEmptyList() {
        given(mapperUtil.mapList(diagnosisRepository.findAll(), DiagnosisResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), diagnosisService.getAllDiagnoses());
    }

    @Test
    void testGetDiagnosisById_withDiagnosis_shouldReturnDiagnosis() {
        DiagnosisResponse expected = mapperUtil.modelMapper().map(diagnosis1, DiagnosisResponse.class);

        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.of(diagnosis1));

        DiagnosisResponse actual = diagnosisService.getDiagnosisById(diagnosis1.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetDiagnosisById_withNoSuchDiagnosis_shouldThrow() {
        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> diagnosisService.getDiagnosisById(1L));
    }

    @Test
    void testUpdateDiagnosis_withDiagnosis_shouldUpdateDiagnosis() {
        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.of(diagnosis2));

        Diagnosis expectedUpdated = new Diagnosis();
        ReflectionTestUtils.setField(expectedUpdated, "id", 2L);
        ReflectionTestUtils.setField(expectedUpdated, "name", "covid");

        DiagnosisResponse expectedDiagnosisResponse =
                mapperUtil.modelMapper().map(expectedUpdated, DiagnosisResponse.class);

        given(diagnosisRepository.save(any())).willReturn(diagnosis2);

        DiagnosisResponse actualDiagnosisResponse = diagnosisService
                .updateDiagnosis(diagnosis2.getId(), diagnosisRequest);

        assertEquals(expectedDiagnosisResponse, actualDiagnosisResponse);
    }

    @Test
    void testUpdateDiagnosis_withNoSuchDiagnosis_shouldThrow() {
        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> diagnosisService.updateDiagnosis(diagnosis2.getId(), diagnosisRequest));
        verify(diagnosisRepository, never()).save(any());
    }

    @Test
    void testDeleteDiagnosis_withValidId_shouldDeleteDiagnosis() {
        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.of(diagnosis1));

        diagnosisService.deleteDiagnosis(diagnosis1.getId());

        verify(diagnosisRepository, times(1)).delete(diagnosis1);
    }

    @Test
    void testDeleteDiagnosis_withNoSuchDiagnosis_shouldThrow() {
        given(diagnosisRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> diagnosisService.deleteDiagnosis(diagnosis1.getId()));
        verify(diagnosisRepository, never()).delete(any());
    }
}