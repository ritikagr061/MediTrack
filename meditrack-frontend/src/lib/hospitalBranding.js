export const DEFAULT_PROFILE = {
    hospitalId: null,
    hospitalCode: "citycare",
    hospitalName: "CityCare Hospital",
    logoUrl: "",
    hospitalMessage: "Secure access for patients and hospital teams.",
    primaryColor: "#0F766E",
    secondaryColor: "#E0F2F1",
    isActive: true,
};

function clamp(value, min, max) {
    return Math.min(Math.max(value, min), max);
}

function hexToRgb(hex) {
    if (!hex) {
        return { r: 15, g: 118, b: 110 };
    }

    const clean = hex.replace("#", "").trim();
    const normalized = clean.length === 3
        ? clean.split("").map((char) => char + char).join("")
        : clean;

    const int = Number.parseInt(normalized, 16);
    if (Number.isNaN(int) || normalized.length !== 6) {
        return { r: 15, g: 118, b: 110 };
    }

    return {
        r: (int >> 16) & 255,
        g: (int >> 8) & 255,
        b: int & 255,
    };
}

function rgbToHex({ r, g, b }) {
    return `#${[r, g, b]
        .map((channel) => clamp(Math.round(channel), 0, 255).toString(16).padStart(2, "0"))
        .join("")}`;
}

function mixColors(baseHex, targetHex, amount) {
    const base = hexToRgb(baseHex);
    const target = hexToRgb(targetHex);

    return rgbToHex({
        r: base.r + (target.r - base.r) * amount,
        g: base.g + (target.g - base.g) * amount,
        b: base.b + (target.b - base.b) * amount,
    });
}

export function deriveBrandPalette(primaryColor, secondaryColor) {
    return {
        "--brand-primary": primaryColor,
        "--brand-secondary": secondaryColor,
        "--brand-primary-soft": mixColors(primaryColor, "#ffffff", 0.8),
        "--brand-primary-deep": mixColors(primaryColor, "#07131a", 0.24),
        "--brand-primary-ink": mixColors(primaryColor, "#020617", 0.45),
        "--surface-elevated": `linear-gradient(180deg, ${mixColors(secondaryColor, "#ffffff", 0.72)} 0%, rgba(255,255,255,0.92) 100%)`,
    };
}

export function buildLoginProfileUrl(hospitalCode) {
    return `http://localhost:8000/api/auth/hospitals/code/${encodeURIComponent(hospitalCode)}/login-profile`;
}

export function fallbackProfileForCode(hospitalCode) {
    return {
        ...DEFAULT_PROFILE,
        hospitalCode,
        hospitalName: `${hospitalCode.charAt(0).toUpperCase()}${hospitalCode.slice(1)} Hospital`,
    };
}
