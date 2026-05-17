package com.meditrack.billingservice.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthContext {
    public UUID hospitalId() {
        Jwt jwt = currentJwt();
        String hospitalId = jwt.getClaimAsString("hospitalId");
        if (hospitalId == null || hospitalId.isBlank()) {
            throw new AccessDeniedException("Authenticated user is not assigned to a hospital");
        }
        return UUID.fromString(hospitalId);
    }

    public UUID scopedHospitalId(UUID requestedHospitalId) {
        UUID authenticatedHospitalId = hospitalId();
        if (requestedHospitalId != null && !authenticatedHospitalId.equals(requestedHospitalId)) {
            throw new AccessDeniedException("Requested hospital is outside the authenticated user's scope");
        }
        return authenticatedHospitalId;
    }

    private Jwt currentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("JWT authentication is required");
        }
        return jwt;
    }
}
