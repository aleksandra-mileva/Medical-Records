package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

}
