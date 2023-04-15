package com.example.medicalrecordsproject.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "patients")
public class Patient extends BaseEntity {
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Must provide information whether the patient has health insurance")
    @Column(name = "has_insurance", nullable = false)
    private Boolean hasInsurance;

    @ManyToOne
    private Doctor gp;

    public Patient() {
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

    public Doctor getGp() {
        return gp;
    }

    public void setGp(Doctor gp) {
        this.gp = gp;
    }
}
