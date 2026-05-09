package com.meditrack.authservice.repository;

import com.meditrack.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginSignupRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByEmailId(String emailId);

    boolean existsByEmailId(String emailId);

    boolean existsByUserName(String userName);

    Optional<UserEntity> findByEmailIdIgnoreCaseAndHospitalCodeIgnoreCase(String emailId, String hospitalCode);

    Optional<UserEntity> findByUserNameIgnoreCaseAndHospitalCodeIgnoreCase(String userName, String hospitalCode);

    boolean existsByEmailIdIgnoreCaseAndHospitalCodeIgnoreCase(String emailId, String hospitalCode);

    boolean existsByUserNameIgnoreCaseAndHospitalCodeIgnoreCase(String userName, String hospitalCode);
}
