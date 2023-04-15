package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;
import com.example.medicalrecordsproject.data.dtos.visits.VisitRequest;
import com.example.medicalrecordsproject.data.dtos.visits.VisitResponse;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.exceptions.NegativeIncomeException;
import com.example.medicalrecordsproject.service.impl.VisitServiceImpl;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @MockBean
    private VisitServiceImpl visitService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateVisit_withValidData_shouldReturnDoctorResponse() throws Exception {
        DiagnosisResponse diagnosisResponse = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse, "name", "headache");
        Set<DiagnosisResponse> diagnosisResponses = Set.of(diagnosisResponse);

        VisitRequest visitRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitRequest, "patientId", 1L);
        ReflectionTestUtils.setField(visitRequest, "healthSystemId", 1L);
        ReflectionTestUtils.setField(visitRequest, "doctorId", 1L);
        ReflectionTestUtils.setField(visitRequest, "diagnosesIds", Set.of(1L));
        ReflectionTestUtils.setField(visitRequest, "visitDate", LocalDate.of(2021, 8, 11));

        given(visitService.createVisit(any(VisitRequest.class)))
                .willAnswer((invocation) -> {
                    VisitRequest request = invocation.getArgument(0);
                    VisitResponse response = new VisitResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "patientId", request.getPatientId());
                    ReflectionTestUtils.setField(response, "doctorId", request.getDoctorId());
                    ReflectionTestUtils.setField(response, "diagnoses", diagnosisResponses);
                    ReflectionTestUtils.setField(response, "visitDate", request.getVisitDate());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.patientId", is(1)))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].id", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].name", is("headache")))
                .andExpect(jsonPath("$.visitDate", is("2021-08-11")));
    }

    @Test
    public void testCreateVisit_withInvalidData_shouldThrow() throws Exception {
        Set<Long> diagnosesIds = Set.of(1L, 2L);

        VisitRequest visitRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitRequest, "patientId", 1L);
        ReflectionTestUtils.setField(visitRequest, "healthSystemId", 1L);
        ReflectionTestUtils.setField(visitRequest, "doctorId", 1L);
        ReflectionTestUtils.setField(visitRequest, "diagnosesIds", Set.of(1L));
        ReflectionTestUtils.setField(visitRequest, "visitDate", LocalDate.of(2028, 8, 11));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("The date has to be in the past or today"));
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        DiagnosisResponse diagnosisResponse = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse, "name", "headache");
        Set<DiagnosisResponse> diagnosisResponses = Set.of(diagnosisResponse);

        VisitResponse response = new VisitResponse();
        ReflectionTestUtils.setField(response, "id", 1L);
        ReflectionTestUtils.setField(response, "patientId", 1L);
        ReflectionTestUtils.setField(response, "doctorId", 1L);
        ReflectionTestUtils.setField(response, "diagnoses", diagnosisResponses);
        ReflectionTestUtils.setField(response, "visitDate", LocalDate.of(2021, 8, 11));

        List<VisitResponse> expected = List.of(response);

        given(visitService.getAllVisits()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].patientId", is(1)))
                .andExpect(jsonPath("$[0].doctorId", is(1)))
                .andExpect(jsonPath("$[0].diagnoses[0].id", is(1)))
                .andExpect(jsonPath("$[0].diagnoses[0].name", is("headache")))
                .andExpect(jsonPath("$[0].visitDate", is("2021-08-11")));
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnSingle() throws Exception {
        DiagnosisResponse diagnosisResponse = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse, "name", "headache");
        Set<DiagnosisResponse> diagnosisResponses = Set.of(diagnosisResponse);

        VisitResponse response = new VisitResponse();
        ReflectionTestUtils.setField(response, "id", 1L);
        ReflectionTestUtils.setField(response, "patientId", 1L);
        ReflectionTestUtils.setField(response, "doctorId", 1L);
        ReflectionTestUtils.setField(response, "diagnoses", diagnosisResponses);
        ReflectionTestUtils.setField(response, "visitDate", LocalDate.of(2021, 8, 11));

        given(visitService.getVisitById(1L)).willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.patientId", is(1)))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].id", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].name", is("headache")))
                .andExpect(jsonPath("$.visitDate", is("2021-08-11")));
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(visitService.getVisitById(1L)).willThrow(new EntityNotFoundException("Visit not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visit not found"))
                .andDo(print());
    }

    @Test
    void testUpdateVisit_withValidData_shouldUpdate() throws Exception {
        DiagnosisResponse diagnosisResponse = new DiagnosisResponse();
        ReflectionTestUtils.setField(diagnosisResponse, "id", 1L);
        ReflectionTestUtils.setField(diagnosisResponse, "name", "headache");
        Set<DiagnosisResponse> diagnosisResponses = Set.of(diagnosisResponse);
        Set<Long> specialtiesIds = Set.of(1L);

        VisitRequest visitRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitRequest, "patientId", 1L);
        ReflectionTestUtils.setField(visitRequest, "healthSystemId", 1L);
        ReflectionTestUtils.setField(visitRequest, "doctorId", 1L);
        ReflectionTestUtils.setField(visitRequest, "diagnosesIds", Set.of(1L));
        ReflectionTestUtils.setField(visitRequest, "visitDate", LocalDate.of(2021, 8, 11));

        given(visitService.updateVisit(anyLong(), any(VisitRequest.class)))
                .willAnswer((invocation) -> {
                    VisitRequest request = invocation.getArgument(1);
                    VisitResponse response = new VisitResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "patientId", request.getPatientId());
                    ReflectionTestUtils.setField(response, "doctorId", request.getDoctorId());
                    ReflectionTestUtils.setField(response, "diagnoses", diagnosisResponses);
                    ReflectionTestUtils.setField(response, "visitDate", request.getVisitDate());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/visits/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.patientId", is(1)))
                .andExpect(jsonPath("$.doctorId", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].id", is(1)))
                .andExpect(jsonPath("$.diagnoses[0].name", is("headache")))
                .andExpect(jsonPath("$.visitDate", is("2021-08-11")));
    }

    @Test
    void testUpdateVisit_withInvalidData_shouldThrow() throws Exception {
        Set<Long> diagnosesIds = Set.of(1L, 2L);

        VisitRequest visitRequest = new VisitRequest();
        ReflectionTestUtils.setField(visitRequest, "patientId", 1L);
        ReflectionTestUtils.setField(visitRequest, "healthSystemId", 1L);
        ReflectionTestUtils.setField(visitRequest, "doctorId", 1L);
        ReflectionTestUtils.setField(visitRequest, "diagnosesIds", Set.of(1L));
        ReflectionTestUtils.setField(visitRequest, "visitDate", LocalDate.of(2028, 8, 11));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/visits/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(visitRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("The date has to be in the past or today"));
    }

    @Test
    void testDeleteVisit_withExistingId_shouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(visitService).deleteVisit(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/visits/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteVisit_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Visit not found")).when(visitService).deleteVisit(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/visits/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Visit not found"));
    }

    @Test
    void testGetTotalIncomeFromVisits() throws Exception {
        BigDecimal totalIncome = new BigDecimal(100);

        given(visitService.getTotalIncomeFromVisits()).willReturn(totalIncome);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalIncome.toString()));
    }

    @Test
    void testGetTotalIncomeFromVisitsOfDoctor_withValidDoctorId_shouldReturnTotalIncome() throws Exception {
        Long doctorId = 1L;
        BigDecimal totalIncome = BigDecimal.valueOf(100);

        given(visitService.getTotalIncomeFromVisitsOfDoctor(doctorId)).willReturn(totalIncome);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-doctor/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().string(totalIncome.toString()));
    }

    @Test
    void testGetTotalIncomeFromVisitsOfDoctor_withInvalidDoctorId_shouldReturnNotFound() throws Exception {
        Long doctorId = 1L;

        given(visitService.getTotalIncomeFromVisitsOfDoctor(doctorId))
                .willThrow(new EntityNotFoundException("Doctor not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-doctor/{doctorId}", doctorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }

    @Test
    void testGetCountVisitsOfPatient_withValidPatientId_shouldReturnVisitCount() throws Exception {
        Long patientId = 1L;
        int visitCount = 3;

        given(visitService.getCountVisitsOfPatient(patientId)).willReturn(visitCount);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-visits-by-patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(visitCount)));
    }

    @Test
    void testGetCountVisitsOfPatient_withInvalidPatientId_shouldReturnNotFound() throws Exception {
        Long patientId = 11L;

        given(visitService.getCountVisitsOfPatient(patientId))
                .willThrow(new EntityNotFoundException("Patient not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-visits-by-patient/{patientId}", patientId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void testGetCountVisitsByDiagnosis_withValidDiagnosisId_shouldReturnVisitCount() throws Exception {
        Long diagnosisId = 1L;
        int visitCount = 3;

        given(visitService.getCountVisitsByDiagnosis(diagnosisId)).willReturn(visitCount);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-visits-by-diagnosis/{diagnosisId}", diagnosisId))
                .andExpect(status().isOk())
                .andExpect(content().string(Integer.toString(visitCount)));
    }

    @Test
    void testGetCountVisitsByDiagnosis_withInvalidDiagnosisId_shouldReturnNotFound() throws Exception {
        Long diagnosisId = 11L;

        given(visitService.getCountVisitsByDiagnosis(diagnosisId))
                .willThrow(new EntityNotFoundException("Diagnosis not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-visits-by-diagnosis/{diagnosisId}", diagnosisId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Diagnosis not found"));
    }

    @Test
    void testGetCountDoctorsBiggerIncome_withValidMinIncome_shouldReturnCount() throws Exception {
        BigDecimal minIncome = BigDecimal.valueOf(15);
        int expectedCount = 3;

        given(visitService.getCountDoctorsBiggerIncome(minIncome)).willReturn(expectedCount);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-doctors-by-income/{minIncome}", minIncome))
                .andExpect(status().isOk())
                .andExpect(content().string(Integer.toString(expectedCount)));
    }

    @Test
    void testGetCountDoctorsBiggerIncome_withNegativeMinIncome_shouldReturnNotFound() throws Exception {
        BigDecimal minIncome = BigDecimal.valueOf(15);

        given(visitService.getCountDoctorsBiggerIncome(minIncome))
                .willThrow(new NegativeIncomeException("The minimal income cannot be negative"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/count-doctors-by-income/{minIncome}", minIncome))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("The minimal income cannot be negative"));
    }

    @Test
    void testGetTotalIncomeFromVisitsByDiagnosis_withValidDiagnosisId_shouldReturnTotalIncome() throws Exception {
        Long diagnosisId = 1L;
        BigDecimal totalIncome = BigDecimal.valueOf(100);

        given(visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosisId)).willReturn(totalIncome);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-diagnosis/{diagnosisId}", diagnosisId))
                .andExpect(status().isOk())
                .andExpect(content().string(totalIncome.toString()));
    }

    @Test
    void testGetTotalIncomeFromVisitsByDiagnosis_withInvalidDiagnosisId_shouldThrowException() throws Exception {
        Long diagnosisId = 1L;

        given(visitService.getTotalIncomeFromVisitsByDiagnosis(diagnosisId))
                .willThrow(new EntityNotFoundException("Diagnosis not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-diagnosis/{diagnosisId}", diagnosisId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Diagnosis not found"));
    }

    @Test
    void testGetTotalIncomeFromPatientsNoInsurance_shouldReturnTotalIncome() throws Exception {
        BigDecimal totalIncome = BigDecimal.valueOf(100);

        given(visitService.getTotalIncomeFromPatientsNoInsurance()).willReturn(totalIncome);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-patients-no-insurance"))
                .andExpect(status().isOk())
                .andExpect(content().string(totalIncome.toString()));
    }

    @Test
    void testGetTotalIncomeByDoctorInsuredPatients_withValidDoctorId_shouldReturnTotalIncome() throws Exception {
        Long doctorId = 1L;
        BigDecimal totalIncome = BigDecimal.valueOf(100);

        given(visitService.getTotalIncomeByDoctorInsuredPatients(doctorId)).willReturn(totalIncome);


        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-doctor-insured-patients/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(content().string(totalIncome.toString()));
    }

    @Test
    void testGetTotalIncomeByDoctorInsuredPatients_withInvalidDoctorId_shouldReturnNotFound() throws Exception {
        Long doctorId = 100L;

        given(visitService.getTotalIncomeByDoctorInsuredPatients(doctorId))
                .willThrow(new EntityNotFoundException("Doctor not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/visits/total-income-by-doctor-insured-patients/{doctorId}", doctorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }
}