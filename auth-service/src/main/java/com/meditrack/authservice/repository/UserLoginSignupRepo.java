package com.meditrack.authservice.repository;

import com.meditrack.authservice.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginSignupRepo extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUserName(String userName);
    public Optional<UserEntity> findByEmailId(String emailId);
    public boolean existsByEmailId(String emailId);
    public boolean existsByUserName(String userName);
}
