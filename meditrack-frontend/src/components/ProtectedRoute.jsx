import { Navigate, Outlet, useLocation } from "react-router-dom";
import { getSession } from "../lib/authSession.js";

export default function ProtectedRoute() {
    const location = useLocation();
    const session = getSession();

    if (!session?.token) {
        return <Navigate to="/login/citycare" replace state={{ from: location }} />;
    }

    return <Outlet />;
}
