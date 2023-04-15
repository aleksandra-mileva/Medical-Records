package com.example.medicalrecordsproject.service;

import com.example.medicalrecordsproject.data.dtos.doctors.DoctorRequest;
import com.example.medicalrecordsproject.data.dtos.doctors.DoctorResponse;

import java.util.List;

public interface DoctorService {
    DoctorResponse createDoctor(DoctorRequest doctorRequest);

    List<DoctorResponse> getAllDoctors();

    DoctorResponse getDoctorById(Long id);

    DoctorResponse updateDoctor(Long id, DoctorRequest doctorRequest);

    void deleteDoctor(Long id);
}
