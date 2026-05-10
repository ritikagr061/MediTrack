import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

export default function RegisterForm({ hospitalCode, hospitalName, hospitalId, hospitalActive }) {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        fullName: "",
        userName: "",
        emailId: "",
        phoneNumber: "",
        password: "",
    });
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const onChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({
            ...current,
            [name]: value,
        }));
    };

    const onSubmit = async (event) => {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!hospitalActive) {
            setError("This hospital registration is currently inactive. Please contact your administrator.");
            return;
        }

        setSubmitting(true);

        try {
            const response = await fetch("http://localhost:8000/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    ...form,
                    hospitalCode,
                    hospitalId,
                }),
            });

            const data = await response.json().catch(() => null);
            if (!response.ok || data?.mainCode !== 200) {
                throw new Error(data?.errorMessage || data?.message || "Unable to register this account.");
            }

            setSuccess("Patient account created successfully. Redirecting to login...");
            setTimeout(() => {
                navigate(`/login/${hospitalCode}`, { replace: true });
            }, 1100);
        } catch (submitError) {
            setError(submitError.message || "Unable to register this account.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <label className="login-form__field">
                <span className="login-form__label">Full Name</span>
                <input
                    className="login-form__input"
                    type="text"
                    name="fullName"
                    placeholder="Enter your name"
                    value={form.fullName}
                    onChange={onChange}
                    required
                />
            </label>

            <label className="login-form__field">
                <span className="login-form__label">User Name</span>
                <input
                    className="login-form__input"
                    type="text"
                    name="userName"
                    placeholder="Choose a user name"
                    value={form.userName}
                    onChange={onChange}
                    required
                />
            </label>

            <label className="login-form__field">
                <span className="login-form__label">Email</span>
                <input
                    className="login-form__input"
                    type="email"
                    name="emailId"
                    placeholder="name@hospital.com"
                    value={form.emailId}
                    onChange={onChange}
                    required
                />
            </label>

            <label className="login-form__field">
                <span className="login-form__label">Phone Number</span>
                <input
                    className="login-form__input"
                    type="tel"
                    name="phoneNumber"
                    placeholder="Enter your mobile number"
                    value={form.phoneNumber}
                    onChange={onChange}
                    required
                />
            </label>

            <label className="login-form__field">
                <span className="login-form__label">Password</span>
                <input
                    className="login-form__input"
                    type="password"
                    name="password"
                    placeholder="Create a password"
                    value={form.password}
                    onChange={onChange}
                    required
                    minLength={6}
                />
            </label>

            {error ? <div role="alert" className="login-form__error">{error}</div> : null}
            {success ? <div role="status" className="login-form__success">{success}</div> : null}

            <button type="submit" className="login-form__submit" disabled={submitting}>
                {submitting ? "Creating your patient account..." : `Register as a patient in ${hospitalName}`}
            </button>

            <p className="login-form__switch">
                Already have an account?{" "}
                <Link to={`/login/${hospitalCode}`} className="login-form__switch-link">
                    Back to sign in
                </Link>
            </p>
        </form>
    );
}
