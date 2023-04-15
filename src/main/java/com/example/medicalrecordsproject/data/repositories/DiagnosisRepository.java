package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

}
