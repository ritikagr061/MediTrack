package com.meditrack.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponse {
    private int mainCode;
    private String errorMessage;
    private String message;
    private String userName;

}
