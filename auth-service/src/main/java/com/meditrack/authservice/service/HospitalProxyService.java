package com.meditrack.authservice.service;

import com.meditrack.authservice.dto.HospitalLoginProfileResponse;
import com.meditrack.authservice.dto.HospitalSyncEventPayload;
import com.meditrack.authservice.entity.HospitalProxy;
import com.meditrack.authservice.entity.UserEntity;
import com.meditrack.authservice.entity.UserRole;
import com.meditrack.authservice.exception.HospitalProxyNotFoundException;
import com.meditrack.authservice.repository.HospitalProxyRepository;
import com.meditrack.authservice.repository.UserLoginSignupRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class HospitalProxyService {
    private final HospitalProxyRepository hospitalProxyRepository;
    private final UserLoginSignupRepo userLoginSignupRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(11);

    public HospitalProxyService(HospitalProxyRepository hospitalProxyRepository,
                                UserLoginSignupRepo userLoginSignupRepo) {
        this.hospitalProxyRepository = hospitalProxyRepository;
        this.userLoginSignupRepo = userLoginSignupRepo;
    }

    public HospitalLoginProfileResponse getByHospitalId(UUID hospitalId) {
        HospitalProxy hospitalProxy = hospitalProxyRepository.findById(hospitalId)
                .orElseThrow(() -> new HospitalProxyNotFoundException("Hospital profile not found for id " + hospitalId));
        return toResponse(hospitalProxy);
    }

    public HospitalLoginProfileResponse getByHospitalCode(String hospitalCode) {
        HospitalProxy hospitalProxy = hospitalProxyRepository.findByHospitalCodeIgnoreCase(hospitalCode)
                .orElseThrow(() -> new HospitalProxyNotFoundException("Hospital profile not found for code " + hospitalCode));
        return toResponse(hospitalProxy);
    }

    @PostConstruct
    @Transactional
    public void provisionDefaultsForExistingHospitals() {
        List<HospitalProxy> proxies = hospitalProxyRepository.findAll();
        proxies.forEach(this::provisionDefaultAccounts);
    }

    @Transactional
    public void upsertFromSyncEvent(HospitalSyncEventPayload payload) {
        HospitalProxy hospitalProxy = hospitalProxyRepository.findById(payload.getHospitalId())
                .orElse(HospitalProxy.builder().hospitalId(payload.getHospitalId()).build());

        hospitalProxy.setHospitalCode(payload.getHospitalCode());
        hospitalProxy.setHospitalName(payload.getHospitalName());
        hospitalProxy.setLogoUrl(payload.getLogoUrl());
        hospitalProxy.setHospitalMessage(payload.getLoginWelcomeText());
        hospitalProxy.setPrimaryColor(payload.getPrimaryColor());
        hospitalProxy.setSecondaryColor(payload.getSecondaryColor());
        hospitalProxy.setActive(payload.isActive());
        hospitalProxy.setLastSyncedAt(payload.getOccurredAt() != null
                ? payload.getOccurredAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                : LocalDateTime.now());

        HospitalProxy savedProxy = hospitalProxyRepository.save(hospitalProxy);
        provisionDefaultAccounts(savedProxy);
    }

    private void provisionDefaultAccounts(HospitalProxy hospitalProxy) {
        createIfAbsent(hospitalProxy, "guestAdmin", "Guest Admin", UserRole.ADMIN);
        createIfAbsent(hospitalProxy, "admin", "Hospital Admin", UserRole.ADMIN);
        createIfAbsent(hospitalProxy, "manager1", "Manager One", UserRole.MANAGER);
        createIfAbsent(hospitalProxy, "manager2", "Manager Two", UserRole.MANAGER);
        createIfAbsent(hospitalProxy, "reception", "Reception Desk", UserRole.RECEPTION);
    }

    private void createIfAbsent(HospitalProxy hospitalProxy, String userName, String fullName, UserRole role) {
        if (userLoginSignupRepo.existsByUserNameIgnoreCaseAndHospitalCodeIgnoreCase(userName, hospitalProxy.getHospitalCode())) {
            return;
        }

        String email = userName.toLowerCase() + "@" + hospitalProxy.getHospitalCode().toLowerCase() + ".demo";
        if (userLoginSignupRepo.existsByEmailIdIgnoreCaseAndHospitalCodeIgnoreCase(email, hospitalProxy.getHospitalCode())) {
            return;
        }

        UserEntity defaultUser = UserEntity.builder()
                .userName(userName)
                .fullName(fullName)
                .emailId(email)
                .phoneNumber("9999999999")
                .password(encoder.encode("12345678"))
                .hospitalId(hospitalProxy.getHospitalId())
                .hospitalCode(hospitalProxy.getHospitalCode())
                .role(role)
                .isActive(hospitalProxy.isActive())
                .build();

        userLoginSignupRepo.save(defaultUser);
    }

    private HospitalLoginProfileResponse toResponse(HospitalProxy hospitalProxy) {
        return new HospitalLoginProfileResponse(
                hospitalProxy.getHospitalId(),
                hospitalProxy.getHospitalCode(),
                hospitalProxy.getHospitalName(),
                hospitalProxy.getLogoUrl(),
                hospitalProxy.getHospitalMessage(),
                hospitalProxy.getPrimaryColor(),
                hospitalProxy.getSecondaryColor(),
                hospitalProxy.isActive()
        );
    }
}
