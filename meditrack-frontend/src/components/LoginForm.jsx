import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { saveSession } from "../lib/authSession.js";

function buildLoginPayload(form, hospitalCode, hospitalId) {
    const identifier = form.identifier.trim();
    return {
        emailId: identifier.includes("@") ? identifier : null,
        userName: identifier.includes("@") ? null : identifier,
        password: form.password,
        hospitalCode,
        hospitalId,
    };
}

export default function LoginForm({ hospitalCode, hospitalName, hospitalId, hospitalActive }) {
    const navigate = useNavigate();
    const [form, setForm] = useState({ identifier: "guestAdmin", password: "12345678", remember: true });
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");

    const onChange = (event) => {
        const { name, value, type, checked } = event.target;
        setForm((current) => ({
            ...current,
            [name]: type === "checkbox" ? checked : value,
        }));
    };

    useEffect(() => {
        setForm((current) => ({
            ...current,
            identifier: "guestAdmin",
            password: "12345678",
        }));
    }, [hospitalCode]);

    const onSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (!hospitalActive) {
            setError("This hospital login is currently inactive. Please contact your administrator.");
            return;
        }

        setSubmitting(true);

        try {
            const response = await fetch("http://localhost:8000/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(buildLoginPayload(form, hospitalCode, hospitalId)),
            });

            const data = await response.json().catch(() => null);
            if (!response.ok || !data?.token) {
                throw new Error(data?.message || "Unable to sign in with the provided credentials.");
            }

            saveSession(data);
            navigate("/home", { replace: true });
        } catch (submitError) {
            setError(submitError.message || "Unable to sign in. Please try again.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <label className="login-form__field">
                <span className="login-form__label">Username or Email</span>
                <input
                    className="login-form__input"
                    type="text"
                    name="identifier"
                    placeholder="guestAdmin or name@hospital.com"
                    value={form.identifier}
                    onChange={onChange}
                    required
                    autoComplete="username"
                />
            </label>

            <label className="login-form__field">
                <span className="login-form__label">Password</span>
                <input
                    className="login-form__input"
                    type="password"
                    name="password"
                    placeholder="Enter your password"
                    value={form.password}
                    onChange={onChange}
                    required
                    autoComplete="current-password"
                    minLength={6}
                />
            </label>

            <div className="login-form__meta">
                <label className="login-form__remember">
                    <input
                        type="checkbox"
                        name="remember"
                        checked={form.remember}
                        onChange={onChange}
                    />
                    Keep me signed in on this device
                </label>

                <a className="login-form__forgot" href="/forgot-password">
                    Forgot password?
                </a>
            </div>

            {error ? (
                <div role="alert" className="login-form__error">
                    {error}
                </div>
            ) : null}

            <button type="submit" className="login-form__submit" disabled={submitting}>
                {submitting ? "Signing you in..." : `Sign in to ${hospitalName}`}
            </button>

            <p className="login-form__hint">
                Hospital code: <strong>{hospitalCode}</strong>
            </p>

            <p className="login-form__hint">
                Demo access is prefilled with <strong>guestAdmin / 12345678</strong>.
            </p>

            <p className="login-form__switch">
                New here?{" "}
                <Link to={`/register/${hospitalCode}`} className="login-form__switch-link">
                    Create an account
                </Link>
            </p>
        </form>
    );
}
