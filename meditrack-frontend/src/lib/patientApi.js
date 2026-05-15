import { apiRequest } from "./apiClient.js";

export function fetchPatients({ hospitalId, search = "", isActive, page = 0, size = 20 }) {
    const params = new URLSearchParams();
    if (hospitalId) {
        params.set("hospitalId", hospitalId);
    }
    if (search.trim()) {
        params.set("search", search.trim());
    }
    if (typeof isActive === "boolean") {
        params.set("isActive", String(isActive));
    }
    params.set("page", String(page));
    params.set("size", String(size));
    params.set("sortBy", "createdAt");
    params.set("sortDirection", "DESC");

    return apiRequest(`/patients?${params.toString()}`);
}

export function fetchPatientById(patientId) {
    return apiRequest(`/patients/${patientId}`);
}

export function fetchPatientSummary(patientId) {
    return apiRequest(`/patients/${patientId}/summary`);
}

export function fetchPatientDiseases(patientId) {
    return apiRequest(`/patients/${patientId}/diseases`);
}

export function createPatient(payload) {
    return apiRequest("/patients", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
