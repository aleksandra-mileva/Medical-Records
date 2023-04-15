package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.doctors.DoctorRequest;
import com.example.medicalrecordsproject.data.dtos.doctors.DoctorResponse;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.impl.DoctorServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @MockBean
    private DoctorServiceImpl doctorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateDoctor_withValidData_shouldCreate() throws Exception {
        SpecialtyResponse specialtyResponse1 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse1, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse1, "name", "cardio");

        SpecialtyResponse specialtyResponse2 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse2, "id", 2L);
        ReflectionTestUtils.setField(specialtyResponse2, "name", "neuro");

        Set<SpecialtyResponse> specialtyResponses = Set.of(specialtyResponse1, specialtyResponse2);
        Set<Long> specialtiesIds = Set.of(1L, 2L);

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setName("John");
        doctorRequest.setBirthdate(LocalDate.of(1990, 11, 7));
        doctorRequest.setSpecialtiesIds(specialtiesIds);
        doctorRequest.setGp(true);

        given(doctorService.createDoctor(any(DoctorRequest.class)))
                .willAnswer((invocation) -> {
                    DoctorRequest request = invocation.getArgument(0);
                    DoctorResponse response = new DoctorResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    ReflectionTestUtils.setField(response, "birthdate", request.getBirthdate());
                    ReflectionTestUtils.setField(response, "specialties", specialtyResponses);
                    ReflectionTestUtils.setField(response, "isGp", request.isGp());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.birthdate", is("1990-11-07")))
                .andExpect(jsonPath("$.specialties[0].id", is(2)))
                .andExpect(jsonPath("$.specialties[0].name", is("neuro")))
                .andExpect(jsonPath("$.specialties[1].id", is(1)))
                .andExpect(jsonPath("$.specialties[1].name", is("cardio")))
                .andExpect(jsonPath("$.gp", is(true)));
    }

    @Test
    public void testCreateDoctor_withInvalidData_shouldThrow() throws Exception {
        Set<Long> specialtiesIds = Set.of(1L, 2L);

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setName("");
        doctorRequest.setBirthdate(LocalDate.of(1990, 11, 7));
        doctorRequest.setSpecialtiesIds(specialtiesIds);
        doctorRequest.setGp(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testRetrieveAll_withRandomData_shouldReturnAll() throws Exception {
        SpecialtyResponse specialtyResponse1 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse1, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse1, "name", "cardio");

        DoctorResponse doctorResponse = new DoctorResponse();
        ReflectionTestUtils.setField(doctorResponse, "id", 1L);
        ReflectionTestUtils.setField(doctorResponse, "name", "John");
        ReflectionTestUtils.setField(doctorResponse, "birthdate", LocalDate.of(1990, 11, 7));
        ReflectionTestUtils.setField(doctorResponse, "specialties", Set.of(specialtyResponse1));
        ReflectionTestUtils.setField(doctorResponse, "isGp", true);

        List<DoctorResponse> expected = List.of(doctorResponse);

        given(doctorService.getAllDoctors()).willReturn(expected);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")))
                .andExpect(jsonPath("$[0].birthdate", is("1990-11-07")))
                .andExpect(jsonPath("$[0].specialties[0].id", is(1)))
                .andExpect(jsonPath("$[0].specialties[0].name", is("cardio")))
                .andExpect(jsonPath("$[0].gp", is(true)));
    }

    @Test
    void testRetrieveSingle_withValidId_shouldReturnSingle() throws Exception {
        SpecialtyResponse specialtyResponse1 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse1, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse1, "name", "cardio");

        DoctorResponse doctorResponse = new DoctorResponse();
        ReflectionTestUtils.setField(doctorResponse, "id", 1L);
        ReflectionTestUtils.setField(doctorResponse, "name", "John");
        ReflectionTestUtils.setField(doctorResponse, "birthdate", LocalDate.of(1990, 11, 7));
        ReflectionTestUtils.setField(doctorResponse, "specialties", Set.of(specialtyResponse1));
        ReflectionTestUtils.setField(doctorResponse, "isGp", true);

        given(doctorService.getDoctorById(1L)).willReturn(doctorResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/doctors/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.birthdate", is("1990-11-07")))
                .andExpect(jsonPath("$.specialties[0].id", is(1)))
                .andExpect(jsonPath("$.specialties[0].name", is("cardio")))
                .andExpect(jsonPath("$.gp", is(true)));
    }

    @Test
    void testRetrieveSingle_notFound_shouldThrow() throws Exception {
        given(doctorService.getDoctorById(1L)).willThrow(new EntityNotFoundException("Doctor not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/doctors/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"))
                .andDo(print());
    }

    @Test
    void testUpdateDoctor_withValidData_shouldUpdate() throws Exception {
        SpecialtyResponse specialtyResponse1 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse1, "id", 1L);
        ReflectionTestUtils.setField(specialtyResponse1, "name", "cardio");

        SpecialtyResponse specialtyResponse2 = new SpecialtyResponse();
        ReflectionTestUtils.setField(specialtyResponse2, "id", 2L);
        ReflectionTestUtils.setField(specialtyResponse2, "name", "neuro");

        Set<SpecialtyResponse> specialtyResponses = Set.of(specialtyResponse1, specialtyResponse2);
        Set<Long> specialtiesIds = Set.of(1L, 2L);

        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setName("John");
        doctorRequest.setBirthdate(LocalDate.of(1990, 11, 7));
        doctorRequest.setSpecialtiesIds(specialtiesIds);
        doctorRequest.setGp(true);

        given(doctorService.updateDoctor(anyLong(), any(DoctorRequest.class)))
                .willAnswer((invocation) -> {
                    DoctorRequest request = invocation.getArgument(1);
                    DoctorResponse response = new DoctorResponse();
                    ReflectionTestUtils.setField(response, "id", 1L);
                    ReflectionTestUtils.setField(response, "name", request.getName());
                    ReflectionTestUtils.setField(response, "birthdate", request.getBirthdate());
                    ReflectionTestUtils.setField(response, "specialties", specialtyResponses);
                    ReflectionTestUtils.setField(response, "isGp", request.isGp());
                    return response;
                });

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/doctors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.birthdate", is("1990-11-07")))
                .andExpect(jsonPath("$.specialties[0].id", is(2)))
                .andExpect(jsonPath("$.specialties[0].name", is("neuro")))
                .andExpect(jsonPath("$.specialties[1].id", is(1)))
                .andExpect(jsonPath("$.specialties[1].name", is("cardio")))
                .andExpect(jsonPath("$.gp", is(true)));
    }

    @Test
    void testUpdateDoctor_withInvalidData_shouldThrow() throws Exception {
        Set<Long> specialtiesIds = Set.of(1L, 2L);
        DoctorRequest doctorRequest = new DoctorRequest();
        doctorRequest.setName("");
        doctorRequest.setBirthdate(LocalDate.of(1990, 11, 7));
        doctorRequest.setSpecialtiesIds(specialtiesIds);
        doctorRequest.setGp(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/doctors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Name cannot be empty"));
    }

    @Test
    void testDeleteDoctor_withExistingId_shouldDelete() throws Exception {
        Long id = 1L;

        doAnswer((invocation) -> {
            return null;
        }).when(doctorService).deleteDoctor(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/doctors/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDoctor_withNonExistingId_shouldReturnNotFound() throws Exception {
        Long id = 1L;

        doThrow(new EntityNotFoundException("Doctor not found")).when(doctorService).deleteDoctor(id);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/doctors/{id}", id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found"));
    }
}