import { apiRequest } from "./apiClient.js";

function addParam(params, key, value) {
    if (value !== undefined && value !== null && value !== "") {
        params.set(key, String(value));
    }
}

export function fetchAppointments({ hospitalId, patientId, doctorId, status, page = 0, size = 20 }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "patientId", patientId);
    addParam(params, "doctorId", doctorId);
    addParam(params, "status", status);
    params.set("page", String(page));
    params.set("size", String(size));

    return apiRequest(`/appointments?${params.toString()}`);
}

export function createAppointment(payload) {
    return apiRequest("/appointments", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export function fetchAppointmentDoctors({ hospitalId, search = "", specialty = "", page = 0, size = 20 }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "search", search.trim());
    addParam(params, "specialty", specialty.trim());
    params.set("page", String(page));
    params.set("size", String(size));

    return apiRequest(`/appointments/doctors?${params.toString()}`);
}

export function fetchDoctorAvailability({ hospitalId, doctorId, date }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "doctorId", doctorId);
    addParam(params, "date", date);

    return apiRequest(`/appointments/availability?${params.toString()}`);
}

export function createDoctorSchedule(payload) {
    return apiRequest("/appointments/doctor-schedules", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
