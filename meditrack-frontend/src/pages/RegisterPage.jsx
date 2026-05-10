import { useEffect, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import RegisterForm from "../components/RegisterForm.jsx";
import {
    buildLoginProfileUrl,
    DEFAULT_PROFILE,
    deriveBrandPalette,
    fallbackProfileForCode,
} from "../lib/hospitalBranding.js";
import "./LoginPage.css";

export default function RegisterPage() {
    const { hospitalCode = DEFAULT_PROFILE.hospitalCode } = useParams();
    const [profile, setProfile] = useState(DEFAULT_PROFILE);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        let isMounted = true;

        async function loadHospitalProfile() {
            try {
                const response = await fetch(buildLoginProfileUrl(hospitalCode));
                if (!response.ok) {
                    throw new Error(`Unable to load hospital profile (${response.status})`);
                }

                const data = await response.json();
                if (!isMounted) {
                    return;
                }

                setProfile({
                    hospitalId: data.hospitalId,
                    hospitalCode: data.hospitalCode || hospitalCode,
                    hospitalName: data.hospitalName || DEFAULT_PROFILE.hospitalName,
                    logoUrl: data.logoUrl || "",
                    hospitalMessage: data.hospitalMessage || DEFAULT_PROFILE.hospitalMessage,
                    primaryColor: data.primaryColor || DEFAULT_PROFILE.primaryColor,
                    secondaryColor: data.secondaryColor || DEFAULT_PROFILE.secondaryColor,
                    isActive: data.isActive ?? true,
                });
            } catch (fetchError) {
                if (!isMounted) {
                    return;
                }

                setProfile(fallbackProfileForCode(hospitalCode));
                setError(fetchError.message || "Unable to load hospital profile.");
            } finally {
                if (isMounted) {
                    setLoading(false);
                }
            }
        }

        loadHospitalProfile();
        return () => {
            isMounted = false;
        };
    }, [hospitalCode]);

    useEffect(() => {
        document.title = `${profile.hospitalName} Register | MediTrack`;
    }, [profile.hospitalName]);

    const brandStyle = useMemo(
        () => deriveBrandPalette(profile.primaryColor, profile.secondaryColor),
        [profile.primaryColor, profile.secondaryColor]
    );

    const hospitalInitial = useMemo(
        () => (profile.hospitalName || "H").trim().charAt(0).toUpperCase(),
        [profile.hospitalName]
    );

    return (
        <main className="login-shell" style={brandStyle}>
            <section className="login-shell__frame">
                <aside className="login-brand-panel">
                    <div className="login-brand-panel__top">
                        <span className="login-brand-panel__eyebrow">Create a hospital account</span>

                        {profile.logoUrl ? (
                            <img className="login-brand-panel__logo" src={profile.logoUrl} alt={`${profile.hospitalName} logo`} />
                        ) : (
                            <div className="login-brand-panel__logo-fallback" aria-hidden="true">
                                {hospitalInitial}
                            </div>
                        )}

                        <div>
                            <h1>{profile.hospitalName}</h1>
                            <p>Register once, then land in a role-specific care workspace designed for your part of the journey.</p>
                        </div>
                    </div>

                    <div className="login-brand-panel__bottom">
                        <div className="login-brand-stat">
                            <strong>Patients</strong>
                            <span>Review diagnosis summaries and stay informed about care progress.</span>
                        </div>
                        <div className="login-brand-stat">
                            <strong>Clinicians</strong>
                            <span>Access role-aware workspaces for appointments, encounters, and coordination.</span>
                        </div>
                        <div className="login-brand-stat">
                            <strong>Operations teams</strong>
                            <span>Support admin, reception, and care workflows under one hospital identity.</span>
                        </div>
                    </div>
                </aside>

                <section className="login-form-panel">
                    {loading ? (
                        <div className="login-state">
                            <div className="login-state-card login-state-card--loading">
                                <div className="login-state-card__spinner" aria-hidden="true" />
                                <h2 className="login-state-card__title">Preparing registration</h2>
                                <p className="login-state-card__text">Loading account setup details for {hospitalCode}.</p>
                            </div>
                        </div>
                    ) : error && !profile.hospitalId ? (
                        <div className="login-state">
                            <div className="login-state-card login-state-card--error">
                                <h2 className="login-state-card__title">Registration unavailable</h2>
                                <p className="login-state-card__text">{error}</p>
                            </div>
                        </div>
                    ) : (
                        <div className="login-form-card login-form-card--wide">
                            <div className="login-form-card__header">
                                <span className="login-form-card__kicker">Hospital Registration</span>
                                <h2 className="login-form-card__title">Create your access</h2>
                                <p className="login-form-card__subtitle">
                                    Register for the correct hospital and role before signing in.
                                </p>
                            </div>

                            <RegisterForm
                                hospitalCode={profile.hospitalCode}
                                hospitalName={profile.hospitalName}
                                hospitalId={profile.hospitalId}
                                hospitalActive={profile.isActive}
                            />
                        </div>
                    )}
                </section>
            </section>
        </main>
    );
}
