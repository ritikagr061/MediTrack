package com.meditrack.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="userDetails")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String userName;
    @NonNull
    private String emailId;
    @NonNull
    private String phoneNumber;
    @NonNull
    private String password;
    private String membership;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    // Automatically set when saving the entity for the first time
    @PrePersist
    protected void onCreate(){
        this.createdAt=LocalDateTime.now();
        this.lastUpdatedAt=LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.lastUpdatedAt=LocalDateTime.now();
    }

}
