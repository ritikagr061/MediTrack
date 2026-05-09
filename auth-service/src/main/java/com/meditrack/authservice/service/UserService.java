package com.meditrack.authservice.service;

import com.meditrack.authservice.dto.UserLoginRequest;
import com.meditrack.authservice.dto.UserLoginResponse;
import com.meditrack.authservice.dto.UserRegisterRequest;
import com.meditrack.authservice.dto.UserRegisterResponse;
import com.meditrack.authservice.entity.HospitalProxy;
import com.meditrack.authservice.entity.UserEntity;
import com.meditrack.authservice.entity.UserRole;
import com.meditrack.authservice.jwt.JwtService;
import com.meditrack.authservice.repository.HospitalProxyRepository;
import com.meditrack.authservice.repository.UserLoginSignupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserLoginSignupRepo repo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HospitalProxyRepository hospitalProxyRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public UserLoginResponse login(UserLoginRequest request) {
        UserLoginResponse response = new UserLoginResponse();
        String hospitalCode = normalize(request.getHospitalCode());
        if (hospitalCode == null) {
            response.setMainCode(400);
            response.setMessage("hospitalCode is required");
            return response;
        }

        Optional<HospitalProxy> hospitalProxyOptional = hospitalProxyRepository.findByHospitalCodeIgnoreCase(hospitalCode);
        if (hospitalProxyOptional.isEmpty()) {
            response.setMainCode(404);
            response.setMessage("Hospital login profile not found");
            return response;
        }

        HospitalProxy hospitalProxy = hospitalProxyOptional.get();
        if (!hospitalProxy.isActive()) {
            response.setMainCode(403);
            response.setMessage("Hospital login is inactive");
            return response;
        }

        Optional<UserEntity> loginUser = findLoginUser(request, hospitalCode);
        if (loginUser.isEmpty() || !loginUser.get().isActive()) {
            response.setMainCode(404);
            response.setMessage("User not found");
            return response;
        }

        UserEntity currentUser = loginUser.get();
        if (!encoder.matches(request.getPassword(), currentUser.getPassword())) {
            response.setMainCode(401);
            response.setMessage("Invalid credentials");
            return response;
        }

        response.setMainCode(200);
        response.setMessage("user fetched successfully");
        response.setUserName(currentUser.getUserName());
        response.setFullName(currentUser.getFullName());
        response.setEmailId(currentUser.getEmailId());
        response.setHospitalId(currentUser.getHospitalId());
        response.setHospitalCode(currentUser.getHospitalCode());
        response.setHospitalName(hospitalProxy.getHospitalName());
        response.setRole(currentUser.getRole().name());
        response.setRoles(new ArrayList<>(List.of("ROLE_" + currentUser.getRole().name())));
        response.setToken(jwtService.generateToken(currentUser.getUserName(), currentUser));
        return response;
    }

    public UserRegisterResponse saveUser(UserRegisterRequest request) {
        UserRegisterResponse response = new UserRegisterResponse();
        String hospitalCode = normalize(request.getHospitalCode());

        if (hospitalCode == null || request.getHospitalId() == null) {
            response.setMainCode(400);
            response.setMessage("hospitalId and hospitalCode are required");
            response.setErrorMessage("hospitalId and hospitalCode are required");
            return response;
        }

        Optional<HospitalProxy> hospitalProxyOptional = hospitalProxyRepository.findByHospitalCodeIgnoreCase(hospitalCode);
        if (hospitalProxyOptional.isEmpty()) {
            response.setMainCode(404);
            response.setMessage("hospital profile not found");
            response.setErrorMessage("hospital profile not found");
            return response;
        }

        if (!hospitalProxyOptional.get().isActive()) {
            response.setMainCode(403);
            response.setMessage("hospital is inactive");
            response.setErrorMessage("hospital is inactive");
            return response;
        }

        if (repo.existsByUserNameIgnoreCaseAndHospitalCodeIgnoreCase(request.getUserName(), hospitalCode)) {
            response.setMainCode(403);
            response.setMessage("userName has already been taken for this hospital");
            response.setErrorMessage("userName has already been taken for this hospital");
            return response;
        }

        if (repo.existsByEmailIdIgnoreCaseAndHospitalCodeIgnoreCase(request.getEmailId(), hospitalCode)) {
            response.setMainCode(403);
            response.setMessage("account with the email id already exists for this hospital");
            response.setErrorMessage("account with the email id already exists for this hospital");
            return response;
        }

        UserEntity user = UserEntity.builder()
                .userName(request.getUserName())
                .fullName(request.getFullName())
                .emailId(request.getEmailId())
                .phoneNumber(request.getPhoneNumber())
                .password(encoder.encode(request.getPassword()))
                .hospitalId(request.getHospitalId())
                .hospitalCode(hospitalCode)
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();

        UserEntity createdUser = repo.save(user);
        response.setMainCode(200);
        response.setMessage("The account with userName " + createdUser.getUserName() + " has been registered. Please login to your account");
        response.setUserName(createdUser.getUserName());
        response.setFullName(createdUser.getFullName());
        response.setHospitalCode(createdUser.getHospitalCode());
        response.setRole(UserRole.PATIENT.name());
        return response;
    }

    private Optional<UserEntity> findLoginUser(UserLoginRequest request, String hospitalCode) {
        String email = normalize(request.getEmailId());
        if (email != null) {
            Optional<UserEntity> byEmail = repo.findByEmailIdIgnoreCaseAndHospitalCodeIgnoreCase(email, hospitalCode);
            if (byEmail.isPresent()) {
                return byEmail;
            }
        }

        String userName = normalize(request.getUserName());
        if (userName != null) {
            return repo.findByUserNameIgnoreCaseAndHospitalCodeIgnoreCase(userName, hospitalCode);
        }

        return Optional.empty();
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
