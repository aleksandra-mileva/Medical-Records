package com.example.medicalrecordsproject.data.dtos.visits;

import com.example.medicalrecordsproject.data.dtos.diagnoses.DiagnosisResponse;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

public class VisitResponse {

    private Long id;

    private Long patientId;

    private Long doctorId;

    private Set<DiagnosisResponse> diagnoses;

    private LocalDate visitDate;

    public VisitResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<DiagnosisResponse> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(Set<DiagnosisResponse> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitResponse that = (VisitResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(patientId, that.patientId)
                && Objects.equals(doctorId, that.doctorId) && Objects.equals(diagnoses, that.diagnoses)
                && Objects.equals(visitDate, that.visitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, patientId, doctorId, diagnoses, visitDate);
    }
}

