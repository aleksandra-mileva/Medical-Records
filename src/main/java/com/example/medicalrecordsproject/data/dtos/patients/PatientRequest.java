package com.example.medicalrecordsproject.data.dtos.patients;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class PatientRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Must provide information whether the patient has health insurance")
    private Boolean hasInsurance;

    private Long gpId;

    public PatientRequest() {
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
}
