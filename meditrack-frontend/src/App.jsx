import { Navigate, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import "./App.css";

export default function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login/citycare" replace />} />
            <Route path="/login" element={<Navigate to="/login/citycare" replace />} />
            <Route path="/register" element={<Navigate to="/register/citycare" replace />} />
            <Route path="/login/:hospitalCode" element={<LoginPage />} />
            <Route path="/register/:hospitalCode" element={<RegisterPage />} />
            <Route element={<ProtectedRoute />}>
                <Route path="/home" element={<DashboardPage />} />
            </Route>
            <Route path="*" element={<div className="app-not-found">Page not found.</div>} />
        </Routes>
    );
}
