const SESSION_KEY = "meditrack_session";

export function saveSession(loginResponse) {
    const session = {
        token: loginResponse.token,
        userName: loginResponse.userName,
        fullName: loginResponse.fullName,
        emailId: loginResponse.emailId,
        hospitalId: loginResponse.hospitalId,
        hospitalCode: loginResponse.hospitalCode,
        hospitalName: loginResponse.hospitalName,
        role: loginResponse.role,
        roles: loginResponse.roles || [],
    };

    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    return session;
}

export function getSession() {
    try {
        const raw = localStorage.getItem(SESSION_KEY);
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
}

export function clearSession() {
    localStorage.removeItem(SESSION_KEY);
}
