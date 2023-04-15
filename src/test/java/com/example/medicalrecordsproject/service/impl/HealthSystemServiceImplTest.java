package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemResponse;
import com.example.medicalrecordsproject.data.entities.HealthSystem;
import com.example.medicalrecordsproject.data.repositories.HealthSystemRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthSystemServiceImplTest {

    @Mock
    private HealthSystemRepository healthSystemRepository;

    @Spy
    private MapperUtil mapperUtil;

    @InjectMocks
    private HealthSystemServiceImpl healthSystemService;

    private HealthSystem healthSystem;
    private HealthSystemRequest healthSystemRequest;

    @BeforeEach
    public void setup() {
        healthSystem = Helpers.getHealthSystem();
        ReflectionTestUtils.setField(healthSystem, "id", 1L);

        healthSystemRequest = Helpers.getHealthSystemRequest();
    }

    @Test
    void testCreateHealthSystem_withHealthSystemRequest_shouldAddNewHealthSystem() {
        given(healthSystemRepository.save(any())).willReturn(healthSystem);

        HealthSystemResponse expectedHealthSystemResponse =
                mapperUtil.modelMapper().map(healthSystem, HealthSystemResponse.class);

        HealthSystemResponse actualHealthSystemResponse = healthSystemService.createHealthSystem(healthSystemRequest);

        assertEquals(expectedHealthSystemResponse, actualHealthSystemResponse);
    }

    @Test
    void testGetAllHealthSystems_withHealthSystems_shouldReturnAllHealthSystems() {
        List<HealthSystemResponse> expected = mapperUtil.mapList(List.of(healthSystem), HealthSystemResponse.class);

        given(mapperUtil.mapList(healthSystemRepository.findAll(), HealthSystemResponse.class)).willReturn(expected);

        assertIterableEquals(expected, healthSystemService.getAllHealthSystems());
    }

    @Test
    void testGetAllHealthSystems_withNoHealthSystems_shouldReturnEmptyList() {
        given(mapperUtil.mapList(healthSystemRepository.findAll(), HealthSystemResponse.class)).willReturn(Collections.emptyList());

        assertIterableEquals(Collections.emptyList(), healthSystemService.getAllHealthSystems());
    }

    @Test
    void testGetHealthSystemById_withHealthSystem_shouldReturnHealthSystem() {
        HealthSystemResponse expected = mapperUtil.modelMapper().map(healthSystem, HealthSystemResponse.class);

        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        HealthSystemResponse actual = healthSystemService.getHealthSystemById(healthSystem.getId());

        assertEquals(expected, actual);
    }

    @Test
    void testGetHealthSystemById_withNoSuchHealthSystem_shouldThrow() {
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> healthSystemService.getHealthSystemById(1L));
    }

    @Test
    void testUpdateHealthSystem_withHealthSystem_shouldUpdateHealthSystem() {
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        HealthSystem expectedUpdated = new HealthSystem();
        ReflectionTestUtils.setField(expectedUpdated, "noInsuranceFee", BigDecimal.valueOf(20));
        ReflectionTestUtils.setField(expectedUpdated, "id", 1L);

        HealthSystemResponse expectedHealthSystemResponse =
                mapperUtil.modelMapper().map(expectedUpdated, HealthSystemResponse.class);

        given(healthSystemRepository.save(any())).willReturn(healthSystem);

        HealthSystemResponse actualHealthSystemResponse = healthSystemService
                .updateHealthSystem(healthSystem.getId(), healthSystemRequest);

        assertEquals(expectedHealthSystemResponse, actualHealthSystemResponse);
    }

    @Test
    void testUpdateHealthSystem_withNoSuchHealthSystem_shouldThrow() {
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> healthSystemService.updateHealthSystem(healthSystem.getId(), healthSystemRequest));
        verify(healthSystemRepository, never()).save(any());
    }

    @Test
    void testDeleteHealthSystem_withValidId_shouldDeleteHealthSystem() {
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.of(healthSystem));

        healthSystemService.deleteHealthSystem(healthSystem.getId());

        verify(healthSystemRepository, times(1)).delete(healthSystem);
    }

    @Test
    void testDeleteHealthSystem_withNoSuchHealthSystem_shouldThrow() {
        given(healthSystemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> healthSystemService.deleteHealthSystem(healthSystem.getId()));
        verify(healthSystemRepository, never()).delete(any());
    }
}