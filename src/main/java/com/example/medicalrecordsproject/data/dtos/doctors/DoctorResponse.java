package com.example.medicalrecordsproject.data.dtos.doctors;

import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class DoctorResponse {

    private Long id;

    private String name;

    private LocalDate birthdate;

    private Set<SpecialtyResponse> specialties;

    private Boolean isGp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    public DoctorResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public Set<SpecialtyResponse> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<SpecialtyResponse> specialties) {
        this.specialties = specialties;
    }

    public Boolean isGp() {
        return isGp;
    }

    public void setGp(Boolean gp) {
        isGp = gp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoctorResponse that = (DoctorResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(birthdate, that.birthdate) && Objects.equals(specialties, that.specialties) && Objects.equals(isGp, that.isGp) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthdate, specialties, isGp, message);
    }
}
