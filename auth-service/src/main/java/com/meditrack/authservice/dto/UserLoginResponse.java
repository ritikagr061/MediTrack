package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private int mainCode;
    private String message;
    private String userName;
    private String fullName;
    private String emailId;
    private UUID hospitalId;
    private String hospitalCode;
    private String hospitalName;
    private String role;
    private ArrayList<String> roles;
    private String token;
}
