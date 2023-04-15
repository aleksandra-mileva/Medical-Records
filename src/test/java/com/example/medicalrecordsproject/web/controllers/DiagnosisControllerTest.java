package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisRequest;
import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.impl.DiagnosisServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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


@WebMvcTest(DiagnosisController.class)
class DiagnosisControllerTest {

    @MockBean
    private DiagnosisServiceImpl diagnosisService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDiagnosis_withRandomData_shouldCreate() throws Exception {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        ReflectionTestUtils.setField(diagnosisRequest, "name", "covid");

        given(diagnosisService.createDiagnosis(any(DiagnosisRequest.class)))
                .willAnswer((invocation) -> {
                    DiagnosisRequest request = invocation.getArgument(0);
                    DiagnosisResponse response = new DiagnosisResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/diagnoses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("covid")));
    }

    @Test
    void testCreateDiagnosis_withInvalidData_shouldThrow() throws Exception {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        ReflectionTestUtils.setField(diagnosisRequest, "name", "");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/diagnoses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        DiagnosisResponse diagnosisResponse1 = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse1, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse1, "name", "covid");

        DiagnosisResponse diagnosisResponse2 = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse2, "id", 2L);
        ReflectionTestUtils.setField(diagnosisResponse2, "name", "virus");

        List<DiagnosisResponse> expected = List.of(diagnosisResponse1, diagnosisResponse2);

        given(diagnosisService.getAllDiagnoses()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/diagnoses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("covid")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("virus")))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnSingle() throws Exception {
        DiagnosisResponse diagnosisResponse1 = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse1, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse1, "name", "covid");

        given(diagnosisService.getDiagnosisById(1L)).willReturn(diagnosisResponse1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/diagnoses/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("covid")))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(diagnosisService.getDiagnosisById(1L)).willThrow(new EntityNotFoundException("Diagnosis not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/diagnoses/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Diagnosis not found"))
                .andDo(print());
    }

    @Test
    void testUpdateDiagnosis_withValidData_shouldUpdate() throws Exception {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        ReflectionTestUtils.setField(diagnosisRequest, "name", "headache");

        given(diagnosisService.updateDiagnosis(anyLong(), any(DiagnosisRequest.class)))
                .willAnswer((invocation) -> {
                    DiagnosisRequest request = invocation.getArgument(1);
                    DiagnosisResponse response = new DiagnosisResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/diagnoses/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("headache"));
    }

    @Test
    void testUpdateDiagnosis_withInvalidData_shouldThrow() throws Exception {
        DiagnosisRequest diagnosisRequest = new DiagnosisRequest();
        ReflectionTestUtils.setField(diagnosisRequest, "name", "");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/diagnoses/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diagnosisRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testDeleteDiagnosis_withExistingId_shouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(diagnosisService).deleteDiagnosis(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/diagnoses/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDiagnosis_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Diagnosis not found")).when(diagnosisService).deleteDiagnosis(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/diagnoses/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Diagnosis not found"));
    }
}