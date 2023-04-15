package com.example.medicalrecordsproject.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "health_systems")
public class HealthSystem extends BaseEntity {

    @NotNull(message = "Must provide fee value")
    @Positive(message = "Fee value must be positive")
    @Column(name = "no_insurance_fee", nullable = false)
    private BigDecimal noInsuranceFee;

    public HealthSystem() {
        this.noInsuranceFee = BigDecimal.ZERO;
    }

    public BigDecimal getNoInsuranceFee() {
        return noInsuranceFee;
    }

    public void setNoInsuranceFee(BigDecimal noInsuranceFee) {
        this.noInsuranceFee = noInsuranceFee;
    }
}
