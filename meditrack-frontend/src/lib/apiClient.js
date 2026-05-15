import { getSession } from "./authSession.js";

const API_BASE_URL = "http://localhost:8000/api";

export async function apiRequest(path, options = {}) {
    const session = getSession();
    const headers = new Headers(options.headers || {});

    if (!headers.has("Content-Type") && options.body) {
        headers.set("Content-Type", "application/json");
    }

    if (session?.token) {
        headers.set("Authorization", `Bearer ${session.token}`);
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers,
    });

    const data = await response
        .json()
        .catch(() => null);

    if (!response.ok) {
        const message = data?.message || data?.error || data?.exception || "Request failed";
        const error = new Error(message);
        error.status = response.status;
        error.data = data;
        throw error;
    }

    return data;
}
