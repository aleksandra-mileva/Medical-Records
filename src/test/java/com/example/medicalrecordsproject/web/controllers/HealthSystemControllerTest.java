package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemRequest;
import com.example.medicalrecordsproject.data.dtos.healthSystem.HealthSystemResponse;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.impl.HealthSystemServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthSystemController.class)
class HealthSystemControllerTest {

    @MockBean
    private HealthSystemServiceImpl healthSystemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateHealthSystem_withRandomData_shouldCreate() throws Exception {
        HealthSystemRequest healthSystemRequest = new HealthSystemRequest();
        ReflectionTestUtils.setField(healthSystemRequest, "noInsuranceFee", BigDecimal.valueOf(10));

        given(healthSystemService.createHealthSystem(any(HealthSystemRequest.class)))
                .willAnswer((invocation) -> {
                    HealthSystemRequest request = invocation.getArgument(0);
                    HealthSystemResponse response = new HealthSystemResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "noInsuranceFee", request.getNoInsuranceFee());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/healthSystem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthSystemRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.noInsuranceFee", is(10)));
    }

    @Test
    void testCreateHealthSystem_withInvalidData_shouldThrow() throws Exception {
        HealthSystemRequest healthSystemRequest = new HealthSystemRequest();
        ReflectionTestUtils.setField(healthSystemRequest, "noInsuranceFee", BigDecimal.valueOf(-5));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/healthSystem")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthSystemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Fee value must be positive"));
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        HealthSystemResponse healthSystemResponse1 = new HealthSystemResponse();
        ReflectionTestUtils.setField(healthSystemResponse1, "id", 1L);
        ReflectionTestUtils.setField(healthSystemResponse1, "noInsuranceFee", BigDecimal.valueOf(10));

        HealthSystemResponse healthSystemResponse2 = new HealthSystemResponse();
        ReflectionTestUtils.setField(healthSystemResponse2, "id", 2L);
        ReflectionTestUtils.setField(healthSystemResponse2, "noInsuranceFee", BigDecimal.valueOf(20));

        List<HealthSystemResponse> expected = List.of(healthSystemResponse1, healthSystemResponse2);

        given(healthSystemService.getAllHealthSystems()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/healthSystem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].noInsuranceFee", is(10)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].noInsuranceFee", is(20)))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnSingle() throws Exception {
        HealthSystemResponse healthSystemResponse1 = new HealthSystemResponse();
        ReflectionTestUtils.setField(healthSystemResponse1, "id", 1L);
        ReflectionTestUtils.setField(healthSystemResponse1, "noInsuranceFee", BigDecimal.valueOf(10));

        given(healthSystemService.getHealthSystemById(1L)).willReturn(healthSystemResponse1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/healthSystem/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.noInsuranceFee", is(10)))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(healthSystemService.getHealthSystemById(1L)).willThrow(new EntityNotFoundException("Health System not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/healthSystem/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health System not found"))
                .andDo(print());
    }

    @Test
    void testUpdateHealthSystem_withValidData_shouldUpdate() throws Exception {
        HealthSystemRequest healthSystemRequest = new HealthSystemRequest();
        ReflectionTestUtils.setField(healthSystemRequest, "noInsuranceFee", BigDecimal.valueOf(10));

        given(healthSystemService.updateHealthSystem(anyLong(), any(HealthSystemRequest.class)))
                .willAnswer((invocation) -> {
                    HealthSystemRequest request = invocation.getArgument(1);
                    HealthSystemResponse response = new HealthSystemResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "noInsuranceFee", request.getNoInsuranceFee());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/healthSystem/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthSystemRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.noInsuranceFee").value(10));
    }

    @Test
    void testUpdateHealthSystem_withInvalidData_shouldThrow() throws Exception {
        HealthSystemRequest healthSystemRequest = new HealthSystemRequest();
        ReflectionTestUtils.setField(healthSystemRequest, "noInsuranceFee", BigDecimal.valueOf(-5));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/healthSystem/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthSystemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Fee value must be positive"));
    }

    @Test
    void testDeleteHealthSystem_withExistingId_shouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(healthSystemService).deleteHealthSystem(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/healthSystem/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteHealthSystem_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Health System not found")).when(healthSystemService).deleteHealthSystem(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/healthSystem/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health System not found"));
    }
}