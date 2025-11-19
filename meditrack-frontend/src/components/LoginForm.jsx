import { useState } from "react";

export default function LoginForm() {
    const [form, setForm] = useState({ email: "", password: "", remember: true });
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");

    const onChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm((f) => ({ ...f, [name]: type === "checkbox" ? checked : value }));
    };

    const onSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSubmitting(true);
        try {
            // Replace with your actual login call:
            await fetch("http://localhost:8000/api/auth/login", { method: "POST", headers: { "Content-Type":"application/json" }, body: JSON.stringify(form) })
            await new Promise((r) => setTimeout(r, 600));
            console.log("login payload", form);
            // navigate("/dashboard");
        } catch {
            setError("Unable to sign in. Please try again.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form onSubmit={onSubmit} style={{ display: "grid", gap: 12, marginTop: 16 }}>
            <label style={{ display: "grid", gap: 6 }}>
                <span className="muted">Email</span>
                <input
                    className="input"
                    type="email"
                    name="email"
                    placeholder="you@example.com"
                    value={form.email}
                    onChange={onChange}
                    required
                    autoComplete="email"
                />
            </label>

            <label style={{ display: "grid", gap: 6 }}>
                <span className="muted">Password</span>
                <input
                    className="input"
                    type="password"
                    name="password"
                    placeholder="••••••••"
                    value={form.password}
                    onChange={onChange}
                    required
                    autoComplete="current-password"
                    minLength={6}
                />
            </label>

            <div className="row">
                <label className="muted" style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <input type="checkbox" name="remember" checked={form.remember} onChange={onChange} />
                    Remember me
                </label>
                <a className="link" href="/forgot-password">Forgot password?</a>
            </div>

            {error && (
                <div role="alert" style={{ background: "#fef2f2", color: "#991b1b", padding: 10, borderRadius: 10 }}>
                    {error}
                </div>
            )}

            <button type="submit" className="btn-primary" disabled={submitting}>
                {submitting ? "Signing in…" : "Sign in"}
            </button>
        </form>
    );
}
