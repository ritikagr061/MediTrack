import { useEffect } from "react";
import LoginForm from "../components/LoginForm.jsx";

export default function LoginPage() {
    useEffect(() => { document.title = "Login • meditrack"; }, []);

    return (
        <div style={{ minHeight: "100dvh", display: "grid", placeItems: "center", background: "#f8fafc", padding: 16 }}>
            <div style={{ width: 380, maxWidth: "100%", background: "#fff", padding: 24, borderRadius: 16, boxShadow: "0 8px 24px rgba(0,0,0,0.08)" }}>
                <h1 style={{ margin: 0, fontSize: 22, textAlign: "center" }}>Sign in</h1>
                <LoginForm />
                <style>{`
          .btn-primary { background:#0f172a;color:#fff;border:none;padding:10px 12px;border-radius:10px;cursor:pointer;width:100%;font-weight:600; }
          .btn-primary:disabled { opacity:.6; cursor:not-allowed; }
          .input { width:100%; padding:10px 12px; border:1px solid #e5e7eb; border-radius:10px; outline:none; font-size:14px; }
          .input:focus { border-color:#94a3b8; box-shadow:0 0 0 3px rgba(148,163,184,.2); }
          .row { display:flex; align-items:center; justify-content:space-between; gap:8px; }
          .muted { color:#64748b; font-size:12px; }
          a.link { color:#0f172a; text-decoration:none; font-size:12px; }
          a.link:hover { text-decoration:underline; }
        `}</style>
            </div>
        </div>
    );
}
