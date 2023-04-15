package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.patients.PatientRequest;
import com.example.medicalrecordsproject.data.dtos.patients.PatientResponse;
import com.example.medicalrecordsproject.data.repositories.DoctorRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.impl.PatientServiceImpl;
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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @MockBean
    private PatientServiceImpl patientService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoctorRepository doctorRepository;

    @Test
    void testCreatePatient_withRandomData_shouldCreate() throws Exception {
        PatientRequest patientRequest = new PatientRequest();
        ReflectionTestUtils.setField(patientRequest, "name", "John");
        ReflectionTestUtils.setField(patientRequest, "hasInsurance", true);
        ReflectionTestUtils.setField(patientRequest, "gpId", 1L);

        given(patientService.createPatient(any(PatientRequest.class)))
                .willAnswer((invocation) -> {
                    PatientRequest request = invocation.getArgument(0);
                    PatientResponse response = new PatientResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    ReflectionTestUtils.setField(response, "hasInsurance", request.isHasInsurance());
                    ReflectionTestUtils.setField(response, "gpId", request.getGpId());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.hasInsurance", is(true)))
                .andExpect(jsonPath("$.gpId", is(1)));
    }

    @Test
    void testCreatePatient_withEmptyName_shouldThrow() throws Exception {
        PatientRequest patientRequest = new PatientRequest();
        ReflectionTestUtils.setField(patientRequest, "name", "");
        ReflectionTestUtils.setField(patientRequest, "hasInsurance", true);
        ReflectionTestUtils.setField(patientRequest, "gpId", 1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        PatientResponse patientResponse1 = new PatientResponse();
        ReflectionTestUtils.setField(patientResponse1, "id", 1L);
        ReflectionTestUtils.setField(patientResponse1, "name", "John");
        ReflectionTestUtils.setField(patientResponse1, "hasInsurance", true);
        ReflectionTestUtils.setField(patientResponse1, "gpId", 1L);

        PatientResponse patientResponse2 = new PatientResponse();
        ReflectionTestUtils.setField(patientResponse2, "id", 2L);
        ReflectionTestUtils.setField(patientResponse2, "name", "Martin");
        ReflectionTestUtils.setField(patientResponse2, "hasInsurance", false);
        ReflectionTestUtils.setField(patientResponse2, "gpId", 2L);

        List<PatientResponse> expected = List.of(patientResponse1, patientResponse2);

        given(patientService.getAllPatients()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].hasInsurance", is(true)))
                .andExpect(jsonPath("$[0].gpId", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Martin")))
                .andExpect(jsonPath("$[1].hasInsurance", is(false)))
                .andExpect(jsonPath("$[1].gpId", is(2)));
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnPatient() throws Exception {
        PatientResponse patientResponse = new PatientResponse();
        ReflectionTestUtils.setField(patientResponse, "id", 1L);
        ReflectionTestUtils.setField(patientResponse, "name", "John");
        ReflectionTestUtils.setField(patientResponse, "hasInsurance", true);
        ReflectionTestUtils.setField(patientResponse, "gpId", 1L);

        given(patientService.getPatientById(1L)).willReturn(patientResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.hasInsurance", is(true)))
                .andExpect(jsonPath("$.gpId", is(1)));
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(patientService.getPatientById(1L)).willThrow(new EntityNotFoundException("Patient not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patients/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"))
                .andDo(print());
    }

    @Test
    void testUpdatePatient_withValidData_shouldUpdate() throws Exception {
        PatientRequest patientRequest = new PatientRequest();
        ReflectionTestUtils.setField(patientRequest, "name", "Martin");
        ReflectionTestUtils.setField(patientRequest, "hasInsurance", true);
        ReflectionTestUtils.setField(patientRequest, "gpId", 1L);

        given(patientService.updatePatient(anyLong(), any(PatientRequest.class)))
                .willAnswer((invocation) -> {
                    PatientRequest request = invocation.getArgument(1);
                    PatientResponse response = new PatientResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    ReflectionTestUtils.setField(response, "hasInsurance", request.isHasInsurance());
                    ReflectionTestUtils.setField(response, "gpId", request.getGpId());
                    return response;
                });


        mockMvc.perform(MockMvcRequestBuilders
                        .put("/patients/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Martin"))
                .andExpect(jsonPath("$.hasInsurance").value(true))
                .andExpect(jsonPath("$.gpId").value(1));
    }

    @Test
    void testUpdatePatient_withEmptyName_shouldThrow() throws Exception {
        PatientRequest patientRequest = new PatientRequest();
        patientRequest.setName("");
        patientRequest.setHasInsurance(true);
        patientRequest.setGpId(1L);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/patients/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testDeletePatient_withExistingId_shouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(patientService).deletePatient(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/patients/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePatient_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Patient not found")).when(patientService).deletePatient(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/patients/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void testGetPatientsWithInsurance_shouldReturnListOfPatientsWithInsurance() throws Exception {
        List<PatientResponse> patientList = new ArrayList<>();

        PatientResponse patientResponse1 = new PatientResponse();
        ReflectionTestUtils.setField(patientResponse1, "id", 1L);
        ReflectionTestUtils.setField(patientResponse1, "name", "John");
        ReflectionTestUtils.setField(patientResponse1, "hasInsurance", true);
        ReflectionTestUtils.setField(patientResponse1, "gpId", 2L);
        patientList.add(patientResponse1);

        PatientResponse patientResponse2 = new PatientResponse();
        ReflectionTestUtils.setField(patientResponse2, "id", 2L);
        ReflectionTestUtils.setField(patientResponse2, "name", "Jane");
        ReflectionTestUtils.setField(patientResponse2, "hasInsurance", true);
        ReflectionTestUtils.setField(patientResponse2, "gpId", 3L);
        patientList.add(patientResponse2);

        given(patientService.getPatientsWithInsurance()).willReturn(patientList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patients/has-insurance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].hasInsurance").value(true))
                .andExpect(jsonPath("$[0].gpId").value(2))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane"))
                .andExpect(jsonPath("$[1].hasInsurance").value(true))
                .andExpect(jsonPath("$[1].gpId").value(3));
    }

    @Test
    void testGetPatientsWithoutInsurance_shouldReturnPercentage() throws Exception {
        given(patientService.getPatientsWithoutInsurance())
                .willReturn(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(10), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/patients/percent-without-insurance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("20.00"));
    }
}