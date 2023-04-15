package com.example.medicalrecordsproject.data.dtos.patients;

import java.util.Objects;

public class PatientResponse {

    private Long id;

    private String name;

    private Boolean hasInsurance;

    private Long gpId;

    public PatientResponse() {
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

    public Boolean isHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(Boolean hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public Long getGpId() {
        return gpId;
    }

    public void setGpId(Long gpId) {
        this.gpId = gpId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientResponse that = (PatientResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(hasInsurance, that.hasInsurance) && Objects.equals(gpId, that.gpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, hasInsurance, gpId);
    }
}
