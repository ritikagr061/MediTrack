package com.meditrack.patientservice.repository;

import com.meditrack.patientservice.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query("""
            select p from Patient p
            where (:hospitalId is null or p.hospitalId = :hospitalId)
              and (:isActive is null or p.isActive = :isActive)
              and (
                    :search is null
                    or lower(p.name) like lower(concat('%', :search, '%'))
                    or lower(p.email) like lower(concat('%', :search, '%'))
                    or lower(p.phone) like lower(concat('%', :search, '%'))
                    or lower(p.patientCode) like lower(concat('%', :search, '%'))
                  )
            """)
    Page<Patient> findAllByFilters(@Param("search") String search,
                                   @Param("hospitalId") UUID hospitalId,
                                   @Param("isActive") Boolean isActive,
                                   Pageable pageable);

    boolean existsByHospitalIdAndEmailIgnoreCase(UUID hospitalId, String email);

    boolean existsByHospitalIdAndEmailIgnoreCaseAndIdNot(UUID hospitalId, String email, UUID id);

    Optional<Patient> findByIdAndHospitalId(UUID id, UUID hospitalId);

    @Query("""
            select p from Patient p
            where p.hospitalId = :hospitalId
              and (:excludePatientId is null or p.id <> :excludePatientId)
              and (
                    (:email is not null and lower(p.email) = lower(:email))
                    or (:phone is not null and p.phone = :phone)
                    or (:aadhar is not null and p.aadhar = :aadhar)
                    or (:pan is not null and upper(p.pan) = upper(:pan))
                  )
            order by p.createdAt desc
            """)
    List<Patient> findPotentialDuplicates(@Param("hospitalId") UUID hospitalId,
                                          @Param("email") String email,
                                          @Param("phone") String phone,
                                          @Param("aadhar") String aadhar,
                                          @Param("pan") String pan,
                                          @Param("excludePatientId") UUID excludePatientId);
}
