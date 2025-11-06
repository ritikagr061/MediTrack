package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    private String userName;
    private String emailId;
    private String password;
}
