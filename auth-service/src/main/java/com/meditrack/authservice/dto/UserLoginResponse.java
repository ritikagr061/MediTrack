package com.meditrack.authservice.dto;

import lombok.*;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private int mainCode;
    private String message;
    private String userName;
    private String emailId;
    private ArrayList<String> roles;
    private String token;
}
