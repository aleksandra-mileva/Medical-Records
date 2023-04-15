package com.example.medicalrecordsproject.data.dtos.healthSystem;

import java.math.BigDecimal;
import java.util.Objects;

public class HealthSystemResponse {

    private Long id;

    private BigDecimal noInsuranceFee;

    public HealthSystemResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getNoInsuranceFee() {
        return noInsuranceFee;
    }

    public void setNoInsuranceFee(BigDecimal noInsuranceFee) {
        this.noInsuranceFee = noInsuranceFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthSystemResponse that = (HealthSystemResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(noInsuranceFee, that.noInsuranceFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, noInsuranceFee);
    }
}
