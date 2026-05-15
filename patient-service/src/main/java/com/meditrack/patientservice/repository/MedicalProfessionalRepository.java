package com.meditrack.patientservice.repository;

import com.meditrack.patientservice.model.MedicalProfessional;
import com.meditrack.patientservice.model.ProfessionalRoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalProfessionalRepository extends JpaRepository<MedicalProfessional, UUID> {
    @Query("""
            select mp from MedicalProfessional mp
            where (:hospitalId is null or mp.hospitalId = :hospitalId)
              and (:roleType is null or mp.roleType = :roleType)
              and (:isActive is null or mp.isActive = :isActive)
              and (:specialty = '' or lower(mp.specialty) like lower(concat('%', :specialty, '%')))
              and (
                    :search = ''
                    or lower(mp.name) like lower(concat('%', :search, '%'))
                    or lower(mp.email) like lower(concat('%', :search, '%'))
                    or lower(mp.phone) like lower(concat('%', :search, '%'))
                    or lower(mp.specialty) like lower(concat('%', :search, '%'))
                  )
            """)
    Page<MedicalProfessional> findAllByFilters(@Param("hospitalId") UUID hospitalId,
                                               @Param("roleType") ProfessionalRoleType roleType,
                                               @Param("isActive") Boolean isActive,
                                               @Param("specialty") String specialty,
                                               @Param("search") String search,
                                               Pageable pageable);

    Optional<MedicalProfessional> findByIdAndHospitalId(UUID id, UUID hospitalId);
}
