package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.HealthSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthSystemRepository extends JpaRepository<HealthSystem, Long> {

}
