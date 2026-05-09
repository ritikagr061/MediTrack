package com.meditrack.authservice.controller;

import com.meditrack.authservice.dto.UserLoginRequest;
import com.meditrack.authservice.dto.UserLoginResponse;
import com.meditrack.authservice.dto.UserRegisterRequest;
import com.meditrack.authservice.dto.UserRegisterResponse;
import com.meditrack.authservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserLoginSignup {
    private final UserService userService;

    public UserLoginSignup(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request, HttpServletRequest servletRequest) {
        UserLoginResponse response = userService.login(request);
        if (response.getMainCode() == 200) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (response.getMainCode() == 401) {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        if (response.getMainCode() == 403) {
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        if (response.getMainCode() == 400) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/register-user")
    public ResponseEntity<UserRegisterResponse> registerUser(@RequestBody UserRegisterRequest request, HttpServletRequest servletRequest) {
        UserRegisterResponse response = userService.saveUser(request);
        return ResponseEntity.status(response.getMainCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody UserRegisterRequest request, HttpServletRequest servletRequest) {
        UserRegisterResponse response = userService.saveUser(request);
        return ResponseEntity.status(response.getMainCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }
}
