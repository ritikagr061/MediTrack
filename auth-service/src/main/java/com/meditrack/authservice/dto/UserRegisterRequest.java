package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterRequest {
    private String emailId;
    private String userName;
    private String password;
    private String phoneNumber;
}
