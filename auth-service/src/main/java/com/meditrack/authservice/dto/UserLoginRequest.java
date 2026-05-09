package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginRequest {
    private String userName;
    private String emailId;
    private String password;
    private UUID hospitalId;
    private String hospitalCode;
}
