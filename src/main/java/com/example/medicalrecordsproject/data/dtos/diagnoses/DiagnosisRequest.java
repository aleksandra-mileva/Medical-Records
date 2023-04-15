package com.example.medicalrecordsproject.data.dtos.diagnoses;

import javax.validation.constraints.NotBlank;

public class DiagnosisRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    public DiagnosisRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
