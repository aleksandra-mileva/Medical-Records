package com.example.medicalrecordsproject.data.dtos.specialties;

import javax.validation.constraints.NotBlank;

public class SpecialtyRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    public SpecialtyRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
