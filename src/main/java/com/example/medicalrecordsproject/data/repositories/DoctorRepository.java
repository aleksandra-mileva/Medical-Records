package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

}
