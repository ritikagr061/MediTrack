import { apiRequest } from "./apiClient.js";

function addParam(params, key, value) {
    if (value !== undefined && value !== null && value !== "") {
        params.set(key, String(value));
    }
}

export function fetchEncounters({ hospitalId, patientId, appointmentId, attendingDoctorId, encounterType, status, page = 0, size = 20 }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "patientId", patientId);
    addParam(params, "appointmentId", appointmentId);
    addParam(params, "attendingDoctorId", attendingDoctorId);
    addParam(params, "encounterType", encounterType);
    addParam(params, "status", status);
    params.set("page", String(page));
    params.set("size", String(size));

    return apiRequest(`/encounters?${params.toString()}`);
}

export function createEncounter(payload) {
    return apiRequest("/encounters", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export function updateEncounterStatus(encounterId, status) {
    return apiRequest(`/encounters/${encounterId}/status`, {
        method: "PATCH",
        body: JSON.stringify({ status }),
    });
}
