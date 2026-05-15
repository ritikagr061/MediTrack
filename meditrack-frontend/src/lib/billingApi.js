import { apiRequest } from "./apiClient.js";

function addParam(params, key, value) {
    if (value !== undefined && value !== null && value !== "") {
        params.set(key, String(value));
    }
}

export function fetchInvoices({ hospitalId, patientId, status, page = 0, size = 20 }) {
    const params = new URLSearchParams();
    addParam(params, "hospitalId", hospitalId);
    addParam(params, "patientId", patientId);
    addParam(params, "status", status);
    params.set("page", String(page));
    params.set("size", String(size));

    return apiRequest(`/billing/invoices?${params.toString()}`);
}

export function createInvoice(payload) {
    return apiRequest("/billing/invoices", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}

export function createPayment(payload) {
    return apiRequest("/billing/payments", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}
