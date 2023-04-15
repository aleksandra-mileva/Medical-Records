package com.example.medicalrecordsproject.data.dtos.doctors;


import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

public class DoctorRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "The date has to be in the past")
    private LocalDate birthdate;

    private Set<Long> specialtiesIds;

    @NotNull(message = "Must provide information whether the doctor is general practitioner")
    private Boolean isGp;

    public DoctorRequest() {
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

    public Set<Long> getSpecialtiesIds() {
        return specialtiesIds;
    }

    public void setSpecialtiesIds(Set<Long> specialtiesIds) {
        this.specialtiesIds = specialtiesIds;
    }

    public Boolean isGp() {
        return isGp;
    }

    public void setGp(Boolean gp) {
        isGp = gp;
    }
}
