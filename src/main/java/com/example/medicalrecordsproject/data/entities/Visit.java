package com.example.medicalrecordsproject.data.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    @NotNull(message = "Must provide patient")
    @ManyToOne
    private Patient patient;

    @NotNull(message = "Must provide doctor")
    @ManyToOne
    private Doctor doctor;

    @NotEmpty(message = "Diagnoses cannot be empty")
    @NotNull(message = "Diagnoses cannot be null")
    @ManyToMany
    private Set<Diagnosis> diagnoses;

    @NotNull(message = "Must provide Health System")
    @ManyToOne
    private HealthSystem healthSystem;

    @NotNull(message = "Must provide the date of the visit")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "The date has to be in the past or today")
    private LocalDate visitDate;

    public Visit() {
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Set<Diagnosis> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(Set<Diagnosis> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public HealthSystem getHealthSystem() {
        return healthSystem;
    }

    public void setHealthSystem(HealthSystem healthSystem) {
        this.healthSystem = healthSystem;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
}
