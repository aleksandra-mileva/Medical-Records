package com.example.medicalrecordsproject.data.repositories;

import com.example.medicalrecordsproject.data.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findAllByDoctorId(Long doctorId);

    int countAllByPatientId(Long patientId);

    @Query("SELECT COUNT(v) " +
            "FROM Visit v " +
            "JOIN v.diagnoses d " +
            "WHERE d.id = :diagnosisId ")
    int countAllByDiagnosisId(Long diagnosisId);

//    @Query(value = "SELECT COUNT(*) " +
//            "FROM (SELECT v.doctor_id, SUM(hs.no_insurance_fee) AS income " +
//            "FROM visits v " +
//            "JOIN health_systems hs on v.health_system_id = hs.id " +
//            "GROUP BY doctor_id " +
//            "HAVING income > :minIncome) AS a", nativeQuery = true)
//    Integer getCountDoctorsBiggerIncome(BigDecimal minIncome);


    // The above method uses native SQL native Query and works too.
    // The below method returns List<Object[]> and every Object[] row consists of:
    // row[0] = doctorId, row[1] = sum of fees bigger than the given
    @Query("SELECT v.doctor.id, SUM(hs.noInsuranceFee) " +
            "FROM Visit v " +
            "JOIN v.healthSystem hs " +
            "GROUP BY v.doctor.id " +
            "HAVING SUM(hs.noInsuranceFee) > :minIncome ")
    List<Object[]> getAllDoctorsBiggerIncome(BigDecimal minIncome);

    @Query("SELECT (v) " +
            "FROM Visit v " +
            "JOIN v.diagnoses d " +
            "WHERE d.id = :diagnosisId ")
    List<Visit> findAllVisitsByDiagnosis(Long diagnosisId);

    List<Visit> findAllByPatient_HasInsuranceFalse();

    List<Visit> findAllByDoctorIdAndPatient_HasInsuranceTrue(Long doctorId);
}
