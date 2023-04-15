package com.example.medicalrecordsproject.data.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor extends BaseEntity {
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "The date has to be in the past")
    @Column(name = "birthdate")
    private LocalDate birthdate;

    @ManyToMany
    private Set<Specialty> specialties;

    @NotNull(message = "Must provide information whether the doctor is general practitioner")
    @Column(name = "is_gp", nullable = false)
    private Boolean isGp;

    public Doctor() {
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

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    public Boolean isGp() {
        return isGp;
    }

    public void setGp(Boolean gp) {
        isGp = gp;
    }
}
