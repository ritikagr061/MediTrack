import { apiRequest } from "./apiClient.js";

function addParam(params, key, value) {
    if (value !== undefined && value !== null && value !== "") {
        params.set(key, String(value));
    }
}

export function fetchNotifications({ hospitalId, patientId, status, page = 0, size = 20 }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "patientId", patientId);
    addParam(params, "status", status);
    params.set("page", String(page));
    params.set("size", String(size));

    return apiRequest(`/notifications?${params.toString()}`);
}

export function createNotification(payload) {
    return apiRequest("/notifications", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
