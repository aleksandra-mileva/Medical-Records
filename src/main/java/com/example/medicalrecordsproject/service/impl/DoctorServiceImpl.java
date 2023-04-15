package com.example.medicalrecordsproject.service.impl;

import com.example.medicalrecordsproject.data.dtos.doctors.DoctorRequest;
import com.example.medicalrecordsproject.data.dtos.doctors.DoctorResponse;
import com.example.medicalrecordsproject.data.entities.Doctor;
import com.example.medicalrecordsproject.data.entities.Specialty;
import com.example.medicalrecordsproject.data.repositories.DoctorRepository;
import com.example.medicalrecordsproject.data.repositories.SpecialtyRepository;
import com.example.medicalrecordsproject.exceptions.EntityNotFoundException;
import com.example.medicalrecordsproject.service.DoctorService;
import com.example.medicalrecordsproject.utils.MapperUtil;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final MapperUtil mapperUtil;

    public DoctorServiceImpl(DoctorRepository doctorRepository, SpecialtyRepository specialtyRepository, MapperUtil mapperUtil) {
        this.doctorRepository = doctorRepository;
        this.specialtyRepository = specialtyRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public DoctorResponse createDoctor(DoctorRequest doctorRequest) {
        // If one of the given Specialties doesn't exist -> We don't create the Doctor because Exception is thrown.
        Set<Specialty> specialtiesFromSpecialtiesIds = getSpecialtiesFromSpecialtiesIds(doctorRequest);

        Doctor doctor = new Doctor();
        mapperUtil.modelMapper().map(doctorRequest, doctor);
        doctor.setSpecialties(specialtiesFromSpecialtiesIds);

        return mapperUtil.modelMapper().map(doctorRepository.save(doctor), DoctorResponse.class);
    }

    public Set<Specialty> getSpecialtiesFromSpecialtiesIds(DoctorRequest doctorRequest) {
        Set<Specialty> specialties = new HashSet<>();

        if (doctorRequest.getSpecialtiesIds() != null) {
            for (Long specialtiesId : doctorRequest.getSpecialtiesIds()) {
                specialties.add(specialtyRepository.findById(specialtiesId)
                        .orElseThrow(() -> new EntityNotFoundException("Specialty not found")));
            }
        }

        return specialties;
    }

    @Override
    public List<DoctorResponse> getAllDoctors() {
        return mapperUtil.mapList(doctorRepository.findAll(), DoctorResponse.class);
    }

    @Override
    public DoctorResponse getDoctorById(Long id) {
        return mapperUtil.modelMapper()
                .map(doctorRepository
                                .findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"))
                        , DoctorResponse.class);
    }

    @Override
    public DoctorResponse updateDoctor(Long id, DoctorRequest doctorRequest) {
        Doctor doctor = this.doctorRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        // If one of the given Specialties doesn't exist -> We don't update the Doctor because Exception is thrown
        Set<Specialty> specialtiesFromSpecialtiesIds = getSpecialtiesFromSpecialtiesIds(doctorRequest);

        mapperUtil.modelMapper().map(doctorRequest, doctor);
        doctor.setSpecialties(specialtiesFromSpecialtiesIds);

        return mapperUtil.modelMapper().map(doctorRepository.save(doctor), DoctorResponse.class);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = this.doctorRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        doctorRepository.delete(doctor);
    }
}
