package com.example.medicalrecordsproject.data.dtos.healthSystem;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class HealthSystemRequest {

    @NotNull(message = "Must provide fee value")
    @Positive(message = "Fee value must be positive")
    private BigDecimal noInsuranceFee;

    public HealthSystemRequest() {
    }

    public BigDecimal getNoInsuranceFee() {
        return noInsuranceFee;
    }

    public void setNoInsuranceFee(BigDecimal noInsuranceFee) {
        this.noInsuranceFee = noInsuranceFee;
    }
}
