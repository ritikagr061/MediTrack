package com.meditrack.authservice.repository;

import com.meditrack.authservice.entity.HospitalProxy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HospitalProxyRepository extends JpaRepository<HospitalProxy, UUID> {
    Optional<HospitalProxy> findByHospitalCodeIgnoreCase(String hospitalCode);
}
