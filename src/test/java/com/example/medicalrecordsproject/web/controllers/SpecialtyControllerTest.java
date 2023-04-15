package com.example.medicalrecordsproject.web.controllers;


import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.impl.SpecialtyServiceImpl;
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

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @MockBean
    private SpecialtyServiceImpl specialtyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSpecialty_withRandomData_shouldCreate() throws Exception {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        ReflectionTestUtils.setField(specialtyRequest, "name", "cardio");

        given(specialtyService.createSpecialty(any(SpecialtyRequest.class)))
                .willAnswer((invocation) -> {
                    SpecialtyRequest request = invocation.getArgument(0);
                    SpecialtyResponse response = new SpecialtyResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialtyRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("cardio")));
    }

    @Test
    void testCreateSpecialty_withInvalidData_shouldThrow() throws Exception {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        ReflectionTestUtils.setField(specialtyRequest, "name", "");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialtyRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        SpecialtyResponse specialtyResponse1 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse1, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse1, "name", "cardio");

        SpecialtyResponse specialtyResponse2 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse2, "id", 2L);
        ReflectionTestUtils.setField(specialtyResponse2, "name", "neuro");

        List<SpecialtyResponse> expected = List.of(specialtyResponse1, specialtyResponse2);

        given(specialtyService.getAllSpecialties()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("cardio")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("neuro")))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnSingle() throws Exception {
        SpecialtyResponse specialtyResponse = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse, "name", "cardio");

        given(specialtyService.getSpecialtyById(1L)).willReturn(specialtyResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/specialties/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("cardio")))
                .andDo(print());
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(specialtyService.getSpecialtyById(1L)).willThrow(new EntityNotFoundException("Specialty not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/specialties/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Specialty not found"))
                .andDo(print());
    }

    @Test
    void testUpdateSpecialty_withValidData_shouldUpdate() throws Exception {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        ReflectionTestUtils.setField(specialtyRequest, "name", "cardio");

        given(specialtyService.updateSpecialty(anyLong(), any(SpecialtyRequest.class)))
                .willAnswer((invocation) -> {
                    SpecialtyRequest request = invocation.getArgument(1);
                    SpecialtyResponse response = new SpecialtyResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/specialties/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialtyRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("cardio"));
    }

    @Test
    void testUpdateSpecialty_withInvalidData_shouldThrow() throws Exception {
        SpecialtyRequest specialtyRequest = new SpecialtyRequest();
        ReflectionTestUtils.setField(specialtyRequest, "name", "");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/specialties/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(specialtyRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testDeleteSpecialty_withExistingId_ShouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(specialtyService).deleteSpecialty(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/specialties/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteSpecialty_withNonExistingId_ShouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Specialty not found")).when(specialtyService).deleteSpecialty(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/specialties/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Specialty not found"));
    }
}