package com.meditrack.authservice.dto;

import com.meditrack.authservice.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
    private String emailId;
    private String userName;
    private String fullName;
    private String password;
    private String phoneNumber;
    private UUID hospitalId;
    private String hospitalCode;
    private UserRole role;
}
