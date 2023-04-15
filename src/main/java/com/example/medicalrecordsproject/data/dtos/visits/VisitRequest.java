package com.example.medicalrecordsproject.data.dtos.visits;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

public class VisitRequest {

    @NotNull(message = "Must provide patient")
    private Long patientId;

    @NotNull(message = "Must provide doctor")
    private Long doctorId;

    @NotEmpty(message = "Diagnoses cannot be empty")
    @NotNull(message = "Diagnoses cannot be null")
    private Set<Long> diagnosesIds;

    @NotNull(message = "Must provide Health System")
    private Long healthSystemId;

    @NotNull(message = "Must provide the date of the visit")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "The date has to be in the past or today")
    private LocalDate visitDate;

    public VisitRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Set<Long> getDiagnosesIds() {
        return diagnosesIds;
    }

    public void setDiagnosesIds(Set<Long> diagnosesIds) {
        this.diagnosesIds = diagnosesIds;
    }

    public Long getHealthSystemId() {
        return healthSystemId;
    }

    public void setHealthSystemId(Long healthSystemId) {
        this.healthSystemId = healthSystemId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
}
