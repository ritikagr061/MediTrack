package com.meditrack.appointmentservice.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
public class PatientServiceClient {
    private final RestClient patientServiceRestClient;

    public PatientServiceClient(RestClient patientServiceRestClient) {
        this.patientServiceRestClient = patientServiceRestClient;
    }

    public PatientServicePatient getPatient(UUID patientId) {
        return patientServiceRestClient.get()
                .uri("/patients/{id}", patientId)
                .retrieve()
                .body(PatientServicePatient.class);
    }

    public PatientServiceDoctor getDoctorForHospital(UUID hospitalId, UUID doctorId) {
        return patientServiceRestClient.get()
                .uri("/medical-professionals/{id}/hospitals/{hospitalId}", doctorId, hospitalId)
                .retrieve()
                .body(PatientServiceDoctor.class);
    }

    public PageResponse<PatientServiceDoctor> searchDoctors(UUID hospitalId, String search, String specialty,
                                                            int page, int size) {
        return patientServiceRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/medical-professionals")
                        .queryParam("hospitalId", hospitalId)
                        .queryParam("roleType", "DOCTOR")
                        .queryParam("isActive", true)
                        .queryParamIfPresent("search", java.util.Optional.ofNullable(search))
                        .queryParamIfPresent("specialty", java.util.Optional.ofNullable(specialty))
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public static class PatientServicePatient {
        private UUID id;
        private UUID hospitalId;
        private String name;
        private String email;
        private String phone;
        private boolean active;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(UUID hospitalId) {
            this.hospitalId = hospitalId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    public static class PatientServiceDoctor {
        private UUID id;
        private UUID hospitalId;
        private String name;
        private String roleType;
        private String specialty;
        private String email;
        private String phone;
        private boolean active;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(UUID hospitalId) {
            this.hospitalId = hospitalId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRoleType() {
            return roleType;
        }

        public void setRoleType(String roleType) {
            this.roleType = roleType;
        }

        public String getSpecialty() {
            return specialty;
        }

        public void setSpecialty(String specialty) {
            this.specialty = specialty;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

    public static class PageResponse<T> {
        private List<T> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        public List<T> getContent() {
            return content;
        }

        public void setContent(List<T> content) {
            this.content = content;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}
