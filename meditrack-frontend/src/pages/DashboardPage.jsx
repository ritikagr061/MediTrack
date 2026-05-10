import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearSession, getSession } from "../lib/authSession.js";
import "./DashboardPage.css";

const ROLE_LABELS = {
    PATIENT: "Patient",
    DOCTOR: "Doctor",
    NURSE: "Nurse",
    ADMIN: "Admin",
    MANAGER: "Manager",
    RECEPTION: "Reception",
    STAFF: "Staff",
};

const MODULE_DEFS = {
    dashboard: { label: "Dashboard", icon: "D" },
    patients: { label: "Patients", icon: "P" },
    appointments: { label: "Appointments", icon: "A" },
    encounters: { label: "Encounters", icon: "E" },
    billing: { label: "Billing", icon: "B" },
    reports: { label: "Reports", icon: "R" },
};

const ROLE_NAV = {
    PATIENT: ["dashboard", "appointments", "encounters", "reports"],
    DOCTOR: ["dashboard", "patients", "appointments", "encounters", "reports"],
    NURSE: ["dashboard", "patients", "appointments", "encounters", "reports"],
    ADMIN: ["dashboard", "patients", "appointments", "encounters", "billing", "reports"],
    MANAGER: ["dashboard", "patients", "appointments", "encounters", "billing", "reports"],
    RECEPTION: ["dashboard", "patients", "appointments", "encounters", "billing", "reports"],
    STAFF: ["dashboard", "patients", "appointments", "encounters", "billing", "reports"],
};

const DASHBOARD_DATA = {
    PATIENT: {
        eyebrow: "Care overview",
        title: "Keep your care journey in one place",
        intro: "See what is coming next, what your doctors updated recently, and which reports need your attention.",
        metrics: [
            { label: "Next appointment", value: "Tomorrow", detail: "10:30 AM with Dr. Mehra" },
            { label: "Open encounters", value: "1", detail: "Follow-up consultation in progress" },
            { label: "Reports ready", value: "3", detail: "CBC, prescription, discharge note" },
            { label: "Notifications", value: "2", detail: "A report and one reminder" },
        ],
        quickLinks: [
            { title: "Upcoming appointments", detail: "Track confirmations and reminders", target: "appointments" },
            { title: "Encounter summaries", detail: "Review diagnoses and care plans", target: "encounters" },
            { title: "Reports and attachments", detail: "Open lab reports and prescriptions", target: "reports" },
        ],
        spotlight: {
            title: "What needs attention",
            items: [
                "Confirm your follow-up visit for tomorrow morning.",
                "Read the updated diagnosis note from your latest consultation.",
                "Download the newly uploaded blood test report.",
            ],
        },
    },
    DOCTOR: {
        eyebrow: "Clinical workspace",
        title: "Run your day from queue to encounter",
        intro: "Move quickly between your patients, today’s appointments, open encounters, and reports that need action.",
        metrics: [
            { label: "Patients today", value: "18", detail: "12 consultations and 6 follow-ups" },
            { label: "Waiting now", value: "5", detail: "Average wait time 18 minutes" },
            { label: "Open encounters", value: "3", detail: "Need notes or treatment updates" },
            { label: "Reports pending", value: "4", detail: "Labs and imaging to review" },
        ],
        quickLinks: [
            { title: "My patients", detail: "Open the patient list for your assigned cases", target: "patients" },
            { title: "Today’s appointments", detail: "Check arrivals, waitlist, and schedule", target: "appointments" },
            { title: "Active encounters", detail: "Continue documentation and treatment", target: "encounters" },
        ],
        spotlight: {
            title: "Clinical priorities",
            items: [
                "2 encounter notes are still in draft and should be closed today.",
                "One teleconsult follow-up needs medication confirmation.",
                "A radiology report has been flagged for urgent review.",
            ],
        },
    },
    NURSE: {
        eyebrow: "Care support",
        title: "Stay on top of assigned patients and handoffs",
        intro: "Use the dashboard to track rounds, appointments tied to your unit, encounter updates, and care documentation.",
        metrics: [
            { label: "Assigned patients", value: "12", detail: "3 need observations this hour" },
            { label: "Scheduled visits", value: "9", detail: "Rounds and support tasks" },
            { label: "Encounter tasks", value: "6", detail: "Vitals, notes, coordination" },
            { label: "Reports to file", value: "2", detail: "New attachments this shift" },
        ],
        quickLinks: [
            { title: "Assigned patients", detail: "Open patient summaries for your ward", target: "patients" },
            { title: "Support appointments", detail: "See today’s room and visit flow", target: "appointments" },
            { title: "Care documentation", detail: "Jump into ongoing encounters", target: "encounters" },
        ],
        spotlight: {
            title: "Shift focus",
            items: [
                "Ward B has 3 patients waiting on vitals updates.",
                "One discharge summary needs attachments before handoff.",
                "An encounter comment from Dr. Mehra mentions medication timing.",
            ],
        },
    },
    ADMIN: {
        eyebrow: "Operations control",
        title: "See hospital activity, flow, and workload",
        intro: "Keep registrations, appointments, encounters, reporting, and future billing work visible from one control surface.",
        metrics: [
            { label: "Patients", value: "1,482", detail: "42 added or updated today" },
            { label: "Appointments today", value: "126", detail: "72 completed, 18 waiting" },
            { label: "Encounters active", value: "21", detail: "Across OPD, ER, and teleconsult" },
            { label: "Notifications", value: "7", detail: "Role invites, sync alerts, workflow changes" },
        ],
        quickLinks: [
            { title: "Patient directory", detail: "See all registered patients in the hospital", target: "patients" },
            { title: "Appointment operations", detail: "Monitor bookings, check-ins, and status", target: "appointments" },
            { title: "Encounter oversight", detail: "Track live care activity and outcomes", target: "encounters" },
            { title: "Reports center", detail: "Review exports and hospital-level summaries", target: "reports" },
        ],
        spotlight: {
            title: "Operational highlights",
            items: [
                "Reception queue is above usual for the morning window.",
                "Two default staff accounts still need password changes.",
                "Billing is intentionally marked YNI and is parked for a later build.",
            ],
        },
    },
    MANAGER: {
        eyebrow: "Flow management",
        title: "Balance volume, flow, and team load",
        intro: "Watch where the hospital is slowing down and move between lists for patients, appointments, encounters, and reports.",
        metrics: [
            { label: "Daily flow", value: "86%", detail: "Current operational utilization" },
            { label: "Patients in process", value: "58", detail: "Across reception, clinic, and review" },
            { label: "Encounters open", value: "17", detail: "Require coordination or follow-up" },
            { label: "Reports queued", value: "8", detail: "Operational summaries and exports" },
        ],
        quickLinks: [
            { title: "Patient operations", detail: "Monitor admissions and active follow-up load", target: "patients" },
            { title: "Appointment flow", detail: "Find bottlenecks in today’s schedule", target: "appointments" },
            { title: "Encounter health", detail: "See unfinished and delayed encounters", target: "encounters" },
            { title: "Billing roadmap", detail: "Reserved area for later revenue workflows", target: "billing" },
        ],
        spotlight: {
            title: "Manager notes",
            items: [
                "Clinic A is trending slower than schedule after 2 PM.",
                "One department has higher-than-usual encounter completion delay.",
                "Reports export volume is spiking ahead of the evening review.",
            ],
        },
    },
    RECEPTION: {
        eyebrow: "Front desk",
        title: "Manage arrivals, lookups, and check-ins",
        intro: "Start from the dashboard, then move into patient lookup, appointment flow, encounters, reports, and future billing support.",
        metrics: [
            { label: "Arrivals today", value: "64", detail: "22 still expected" },
            { label: "Patient lookups", value: "19", detail: "Duplicate checks and updates" },
            { label: "Check-ins open", value: "11", detail: "Need room or clinician assignment" },
            { label: "Notifications", value: "4", detail: "One schedule update, three reminders" },
        ],
        quickLinks: [
            { title: "Patient list", detail: "Search, filter, and open hospital records", target: "patients" },
            { title: "Appointment desk", detail: "Handle bookings and check-ins", target: "appointments" },
            { title: "Encounter routing", detail: "Review planned and in-progress visits", target: "encounters" },
            { title: "Billing placeholder", detail: "Future counter workflow area", target: "billing" },
        ],
        spotlight: {
            title: "Front desk focus",
            items: [
                "6 registrations need duplicate verification.",
                "Three appointments were rebooked in the last hour.",
                "One report pickup is waiting at the counter.",
            ],
        },
    },
    STAFF: {
        eyebrow: "Service desk",
        title: "Coordinate day-to-day patient movement",
        intro: "Use the same operational tools as the front desk: patients, appointments, encounters, reports, and the future billing area.",
        metrics: [
            { label: "Patients served", value: "41", detail: "Since start of shift" },
            { label: "Appointments touched", value: "24", detail: "Booked, checked in, or updated" },
            { label: "Encounters routed", value: "8", detail: "Sent to room or clinician" },
            { label: "Notifications", value: "3", detail: "Workflow and queue alerts" },
        ],
        quickLinks: [
            { title: "Patient search", detail: "Move quickly through lookup and updates", target: "patients" },
            { title: "Appointments", detail: "Review schedule and status changes", target: "appointments" },
            { title: "Encounters", detail: "Check the active care queue", target: "encounters" },
            { title: "Reports", detail: "Support attachments and handover", target: "reports" },
        ],
        spotlight: {
            title: "Desk updates",
            items: [
                "The new shift started with a queue spillover from reception.",
                "Two patient records need phone number confirmation.",
                "One encounter moved from planned to in progress in the last ten minutes.",
            ],
        },
    },
};

const MODULE_CONTENT = {
    patients: {
        title: "Patients",
        descriptionByRole: {
            DOCTOR: "Your patients and follow-up cases",
            NURSE: "Assigned and recently supported patients",
            default: "Hospital-wide patient directory",
        },
        filtersByRole: {
            PATIENT: [],
            DOCTOR: [
                { label: "Status", value: "Active" },
                { label: "Clinic", value: "General Medicine" },
                { label: "Risk", value: "All" },
            ],
            NURSE: [
                { label: "Ward", value: "All" },
                { label: "Status", value: "Assigned" },
                { label: "Attention", value: "All" },
            ],
            default: [
                { label: "Patient", value: "All patients" },
                { label: "Status", value: "Active" },
                { label: "Hospital", value: "CityCare" },
                { label: "Updated", value: "Last 30 days" },
            ],
        },
        columns: ["Patient", "Primary need", "Assigned", "Last visit", "Status", "Action"],
        rowsByRole: {
            DOCTOR: [
                ["Riya Kapoor, 28 F", "Post-viral follow-up", "Dr. Mehra", "May 8, 2026", { pill: "Needs review", tone: "warning" }, "Open"],
                ["Aarav Sharma, 42 M", "Respiratory symptoms", "Dr. Mehra", "May 7, 2026", { pill: "Active", tone: "success" }, "Open"],
                ["Nisha Rao, 34 F", "Medication follow-up", "Dr. Mehra", "May 5, 2026", { pill: "Stable", tone: "neutral" }, "Open"],
            ],
            NURSE: [
                ["Kavya Menon, 48 F", "Vitals due", "Ward B", "May 9, 2026", { pill: "Due now", tone: "warning" }, "Open"],
                ["Suresh Patel, 63 M", "Medication support", "Ward B", "May 9, 2026", { pill: "Assigned", tone: "info" }, "Open"],
                ["Leena Joseph, 51 F", "Observation update", "Ward C", "May 8, 2026", { pill: "Watch", tone: "critical" }, "Open"],
            ],
            default: [
                ["John Doe, 51 M", "Chest pain review", "Dr. Sarah Nguyen", "May 8, 2026", { pill: "In follow-up", tone: "info" }, "View"],
                ["Mary Smith, 56 F", "Routine check-up", "Dr. James Wilson", "May 7, 2026", { pill: "Active", tone: "success" }, "View"],
                ["Robert Johnson, 63 M", "Pneumonia care", "Dr. Emily Clark", "May 5, 2026", { pill: "Admitted", tone: "critical" }, "View"],
                ["Emma Wilson, 28 F", "Dermatology consult", "Dr. James Wilson", "May 3, 2026", { pill: "Planned", tone: "neutral" }, "View"],
            ],
        },
        actionByRole: {
            DOCTOR: "Open patient chart",
            NURSE: "Review assignments",
            default: "Add patient",
        },
        apis: ["GET /patients", "GET /hospitals/{hospitalId}/patients", "GET /patients/search", "GET /patients/{id}"],
    },
    appointments: {
        title: "Appointments",
        descriptionByRole: {
            PATIENT: "Your bookings, reminders, and follow-up visits",
            DOCTOR: "Your schedule, queue, and visit flow",
            default: "Hospital appointment operations",
        },
        filtersByRole: {
            PATIENT: [
                { label: "Status", value: "Upcoming" },
                { label: "Visit type", value: "All" },
                { label: "Date", value: "This month" },
            ],
            DOCTOR: [
                { label: "Status", value: "Today" },
                { label: "Visit type", value: "All" },
                { label: "Room", value: "Clinic A" },
                { label: "Date", value: "Today" },
            ],
            default: [
                { label: "Patient", value: "All" },
                { label: "Type", value: "All" },
                { label: "Status", value: "Last 30 days" },
                { label: "Clinician", value: "All clinicians" },
                { label: "Date", value: "May 1 - May 10" },
            ],
        },
        columns: ["Patient", "Type", "Reason", "Status", "Clinician", "Time", "Action"],
        rowsByRole: {
            PATIENT: [
                ["You", "Follow-up", "General consultation", { pill: "Confirmed", tone: "success" }, "Dr. Mehra", "May 11, 10:30 AM", "Open"],
                ["You", "Lab review", "Blood test discussion", { pill: "Planned", tone: "neutral" }, "Dr. Mehra", "May 14, 4:00 PM", "Open"],
            ],
            DOCTOR: [
                ["Aarav Sharma", "OPD", "Cough and fever", { pill: "Waiting", tone: "warning" }, "Dr. Mehra", "10:30 AM", "Open"],
                ["Nisha Rao", "Follow-up", "Medication review", { pill: "Checked in", tone: "info" }, "Dr. Mehra", "11:00 AM", "Open"],
                ["Imran Khan", "Teleconsult", "Lab report follow-up", { pill: "Confirmed", tone: "success" }, "Dr. Mehra", "12:15 PM", "Open"],
            ],
            default: [
                ["John Doe", "ER", "Chest pain", { pill: "Checked in", tone: "warning" }, "Dr. Sarah Nguyen", "Apr 22, 9:20 AM", "View"],
                ["Mary Smith", "OPD", "Routine check up", { pill: "Cancelled", tone: "neutral" }, "Dr. James Wilson", "Apr 20, 2:00 PM", "View"],
                ["Robert Johnson", "Teleconsult", "Pneumonia review", { pill: "Completed", tone: "success" }, "Dr. Emily Clark", "Apr 18, 4:15 PM", "View"],
                ["Emma Wilson", "OPD", "Skin rash", { pill: "Planned", tone: "info" }, "Dr. James Wilson", "Apr 15, 11:30 AM", "View"],
            ],
        },
        actionByRole: {
            PATIENT: "Book appointment",
            DOCTOR: "Open today’s queue",
            default: "New appointment",
        },
        apis: ["GET /appointments", "GET /appointments/me", "GET /appointments/upcoming", "PATCH /appointments/{id}/status"],
    },
    encounters: {
        title: "Encounters",
        descriptionByRole: {
            PATIENT: "Your visit summaries and diagnosis flow",
            DOCTOR: "Your in-progress and completed encounters",
            default: "Encounter listing and workflow visibility",
        },
        filtersByRole: {
            PATIENT: [
                { label: "Type", value: "All" },
                { label: "Status", value: "Recent" },
                { label: "Date", value: "Last 30 days" },
            ],
            DOCTOR: [
                { label: "Patient", value: "All" },
                { label: "Type", value: "All" },
                { label: "Status", value: "In progress" },
                { label: "Location", value: "Clinic A" },
                { label: "Date", value: "Today" },
            ],
            default: [
                { label: "Patient", value: "All" },
                { label: "Type", value: "All" },
                { label: "Status", value: "Last 30 days" },
                { label: "Clinician", value: "All clinicians" },
                { label: "Location", value: "All locations" },
                { label: "Date", value: "May 1 - May 10" },
            ],
        },
        columns: ["Patient", "Type", "Reason", "Status", "Attending", "Location", "Started", "Action"],
        rowsByRole: {
            PATIENT: [
                ["You", "OPD", "Respiratory follow-up", { pill: "In progress", tone: "warning" }, "Dr. Mehra", "Clinic A, Room 2", "May 8, 2026", "Open"],
                ["You", "Teleconsult", "Lab report review", { pill: "Finished", tone: "success" }, "Dr. Mehra", "Virtual", "Apr 28, 2026", "Open"],
            ],
            DOCTOR: [
                ["Riya Kapoor", "OPD", "Medication review", { pill: "In progress", tone: "warning" }, "Dr. Mehra", "Clinic A, Room 2", "10:40 AM", "Open"],
                ["Mohan Das", "Teleconsult", "Follow-up plan", { pill: "Draft", tone: "info" }, "Dr. Mehra", "Virtual", "9:25 AM", "Open"],
                ["Anita Bose", "OPD", "Dermatology review", { pill: "Finished", tone: "success" }, "Dr. Mehra", "Clinic B", "Yesterday", "Open"],
            ],
            default: [
                ["John Doe, 51 M", "ER", "Chest pain", { pill: "In progress", tone: "warning" }, "Dr. Sarah Nguyen", "Emergency Dept", "Apr 22, 2024", "View"],
                ["Mary Smith, 56 F", "OPD", "Routine check up", { pill: "Cancelled", tone: "neutral" }, "Dr. James Wilson", "Clinic A (Room 2)", "Apr 20, 2024", "View"],
                ["Robert Johnson, 63 M", "IPD", "Pneumonia", { pill: "In progress", tone: "success" }, "Dr. Emily Clark", "Ward 3", "Apr 18, 2024", "View"],
                ["Alice Williams, 45 F", "Teleconsult", "Follow-up visit", { pill: "Finished", tone: "neutral" }, "Dr. Michael Brown", "Virtual", "Apr 15, 2024", "View"],
            ],
        },
        actionByRole: {
            PATIENT: "Open latest encounter",
            DOCTOR: "New encounter",
            default: "New encounter",
        },
        apis: ["GET /encounters", "GET /encounters/me", "GET /encounters/{id}", "PATCH /encounters/{id}/status"],
    },
    reports: {
        title: "Reports",
        descriptionByRole: {
            PATIENT: "Your lab reports, prescriptions, and attachments",
            DOCTOR: "Reports linked to your cases and encounters",
            default: "Reports, exports, and uploaded documents",
        },
        filtersByRole: {
            PATIENT: [
                { label: "Category", value: "All" },
                { label: "Status", value: "Ready" },
                { label: "Date", value: "Last 90 days" },
            ],
            DOCTOR: [
                { label: "Patient", value: "All" },
                { label: "Category", value: "Lab and imaging" },
                { label: "Status", value: "Needs review" },
                { label: "Date", value: "This week" },
            ],
            default: [
                { label: "Category", value: "All" },
                { label: "Status", value: "Ready" },
                { label: "Owner", value: "All" },
                { label: "Date", value: "Last 30 days" },
            ],
        },
        columns: ["Report", "Category", "Linked to", "Updated", "Status", "Action"],
        rowsByRole: {
            PATIENT: [
                ["CBC Report", "Lab", "Encounter #20041", "May 8, 2026", { pill: "Ready", tone: "success" }, "Open"],
                ["Prescription", "Medication", "Encounter #20041", "May 8, 2026", { pill: "Ready", tone: "success" }, "Open"],
                ["Discharge advice", "Care plan", "Encounter #19982", "Apr 18, 2026", { pill: "Ready", tone: "neutral" }, "Open"],
            ],
            DOCTOR: [
                ["Radiology review", "Imaging", "Riya Kapoor", "Today, 9:20 AM", { pill: "Needs review", tone: "warning" }, "Open"],
                ["CBC result", "Lab", "Imran Khan", "Today, 8:50 AM", { pill: "Reviewed", tone: "success" }, "Open"],
                ["Discharge summary", "Attachment", "Mohan Das", "Yesterday", { pill: "Shared", tone: "info" }, "Open"],
            ],
            default: [
                ["Encounter export", "Export", "Operational dashboard", "Today, 11:30 AM", { pill: "Ready", tone: "success" }, "Download"],
                ["Pending imaging", "Imaging", "Dr. Sarah Nguyen", "Today, 9:10 AM", { pill: "Needs review", tone: "warning" }, "Open"],
                ["Ward handoff summary", "Attachment", "Ward B", "Yesterday", { pill: "Shared", tone: "info" }, "Open"],
            ],
        },
        actionByRole: {
            PATIENT: "Open latest report",
            DOCTOR: "Review flagged reports",
            default: "Export reports",
        },
        apis: ["GET /reports", "GET /reports/me", "GET /encounters/{id}/attachments", "POST /reports/export"],
    },
    billing: {
        title: "Billing",
        descriptionByRole: {
            default: "Planned workspace for invoicing, payments, and counter operations",
        },
        filtersByRole: {
            default: [
                { label: "Status", value: "Not yet implemented" },
                { label: "Scope", value: "Roadmap" },
            ],
        },
        actionByRole: {
            default: "Billing is YNI",
        },
        apis: ["POST /billing/invoices", "GET /billing/transactions", "PATCH /billing/payments/{id}"],
    },
};

const NOTIFICATIONS = {
    PATIENT: [
        { title: "New report uploaded", meta: "CBC report is ready to view", time: "5 min ago" },
        { title: "Appointment reminder", meta: "Follow-up visit tomorrow at 10:30 AM", time: "1 hr ago" },
    ],
    DOCTOR: [
        { title: "Encounter note still open", meta: "Riya Kapoor encounter needs closure", time: "4 min ago" },
        { title: "Lab result flagged", meta: "Radiology update added for Imran Khan", time: "18 min ago" },
        { title: "Queue update", meta: "Two patients checked in for Clinic A", time: "33 min ago" },
    ],
    NURSE: [
        { title: "Vitals due", meta: "Three patients need updates before noon", time: "7 min ago" },
        { title: "Doctor mention", meta: "Medication timing updated in encounter", time: "21 min ago" },
    ],
    ADMIN: [
        { title: "New patient registrations", meta: "8 profiles added in the last hour", time: "3 min ago" },
        { title: "Staff role review", meta: "Two accounts still on default passwords", time: "27 min ago" },
        { title: "Hospital sync complete", meta: "Branding projection updated in auth", time: "48 min ago" },
    ],
    MANAGER: [
        { title: "Queue pressure rising", meta: "Reception wait time crossed 20 minutes", time: "6 min ago" },
        { title: "Encounter backlog", meta: "Three charts need finalization", time: "25 min ago" },
    ],
    RECEPTION: [
        { title: "New arrival", meta: "Priya Nair reached front desk for registration", time: "2 min ago" },
        { title: "Check-in moved", meta: "Room assignment updated for 11 AM booking", time: "14 min ago" },
    ],
    STAFF: [
        { title: "Lookup request", meta: "Duplicate verification pending for one patient", time: "11 min ago" },
        { title: "Report pickup", meta: "One printed report is waiting at the desk", time: "31 min ago" },
    ],
};

function getRoleLabel(role) {
    return ROLE_LABELS[role] || "Staff";
}

function getInitials(name = "") {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (!parts.length) {
        return "MT";
    }
    return parts.slice(0, 2).map((part) => part.charAt(0).toUpperCase()).join("");
}

function getNavForRole(role) {
    return ROLE_NAV[role] || ROLE_NAV.STAFF;
}

function getDashboardForRole(role) {
    return DASHBOARD_DATA[role] || DASHBOARD_DATA.STAFF;
}

function getNotificationsForRole(role) {
    return NOTIFICATIONS[role] || NOTIFICATIONS.STAFF;
}

function getModuleDescription(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    if (!content) {
        return "";
    }

    return content.descriptionByRole?.[role] || content.descriptionByRole?.default || "";
}

function getModuleFilters(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    if (!content) {
        return [];
    }

    return content.filtersByRole?.[role] || content.filtersByRole?.default || [];
}

function getModuleRows(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    if (!content) {
        return [];
    }

    return content.rowsByRole?.[role] || content.rowsByRole?.default || [];
}

function getModuleAction(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    if (!content) {
        return "";
    }

    return content.actionByRole?.[role] || content.actionByRole?.default || "";
}

function Pill({ value, tone = "neutral" }) {
    return <span className={`workspace-pill workspace-pill--${tone}`}>{value}</span>;
}

function DashboardHero({ dashboard, onOpenModule }) {
    return (
        <section className="workspace-hero">
            <div>
                <span className="workspace-eyebrow">{dashboard.eyebrow}</span>
                <h1>{dashboard.title}</h1>
                <p>{dashboard.intro}</p>
            </div>
            <div className="workspace-hero__actions">
                {dashboard.quickLinks.slice(0, 2).map((link) => (
                    <button key={link.title} type="button" onClick={() => onOpenModule(link.target)}>
                        {MODULE_DEFS[link.target].label}
                    </button>
                ))}
            </div>
        </section>
    );
}

function MetricCards({ metrics }) {
    return (
        <section className="workspace-metrics">
            {metrics.map((metric) => (
                <article className="metric-card" key={metric.label}>
                    <span>{metric.label}</span>
                    <strong>{metric.value}</strong>
                    <p>{metric.detail}</p>
                </article>
            ))}
        </section>
    );
}

function QuickLinks({ links, onOpenModule }) {
    return (
        <section className="workspace-card">
            <div className="workspace-card__header">
                <div>
                    <span className="workspace-section-label">Shortcuts</span>
                    <h2>Where to go next</h2>
                </div>
            </div>
            <div className="shortcut-grid">
                {links.map((link) => (
                    <button
                        key={link.title}
                        type="button"
                        className="shortcut-card"
                        onClick={() => onOpenModule(link.target)}
                    >
                        <div className="shortcut-card__mark">{MODULE_DEFS[link.target].icon}</div>
                        <div>
                            <strong>{link.title}</strong>
                            <p>{link.detail}</p>
                        </div>
                    </button>
                ))}
            </div>
        </section>
    );
}

function Spotlight({ spotlight }) {
    return (
        <section className="workspace-card">
            <div className="workspace-card__header">
                <div>
                    <span className="workspace-section-label">Highlights</span>
                    <h2>{spotlight.title}</h2>
                </div>
            </div>
            <ul className="spotlight-list">
                {spotlight.items.map((item) => (
                    <li key={item}>{item}</li>
                ))}
            </ul>
        </section>
    );
}

function FilterBar({ filters }) {
    return (
        <section className="filter-bar" aria-label="Filters">
            {filters.map((filter) => (
                <label className="filter-pill" key={`${filter.label}-${filter.value}`}>
                    <span>{filter.label}</span>
                    <button type="button">{filter.value}</button>
                </label>
            ))}
        </section>
    );
}

function TableCell({ cell }) {
    if (cell && typeof cell === "object" && "pill" in cell) {
        return <Pill value={cell.pill} tone={cell.tone} />;
    }
    return <span>{cell}</span>;
}

function ModuleTable({ columns, rows }) {
    return (
        <div className="table-shell">
            <table className="workspace-table">
                <thead>
                    <tr>
                        {columns.map((column) => (
                            <th key={column}>{column}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {rows.map((row, index) => (
                        <tr key={`${row[0]}-${index}`}>
                            {row.map((cell, cellIndex) => (
                                <td key={`${row[0]}-${cellIndex}`}>
                                    <TableCell cell={cell} />
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

function ModuleApiPlan({ apis }) {
    return (
        <section className="workspace-card workspace-card--api">
            <div className="workspace-card__header">
                <div>
                    <span className="workspace-section-label">Backend follow-up</span>
                    <h2>APIs this screen will need</h2>
                </div>
            </div>
            <div className="api-chip-grid">
                {apis.map((api) => (
                    <code key={api}>{api}</code>
                ))}
            </div>
        </section>
    );
}

function DashboardView({ role, onOpenModule }) {
    const dashboard = getDashboardForRole(role);

    return (
        <div className="workspace-stack">
            <DashboardHero dashboard={dashboard} onOpenModule={onOpenModule} />
            <MetricCards metrics={dashboard.metrics} />
            <div className="workspace-grid workspace-grid--dashboard">
                <QuickLinks links={dashboard.quickLinks} onOpenModule={onOpenModule} />
                <Spotlight spotlight={dashboard.spotlight} />
            </div>
        </div>
    );
}

function ListModuleView({ moduleKey, role }) {
    const content = MODULE_CONTENT[moduleKey];
    const rows = getModuleRows(moduleKey, role);
    const action = getModuleAction(moduleKey, role);

    if (moduleKey === "billing") {
        return (
            <div className="workspace-stack">
                <section className="module-heading">
                    <div>
                        <span className="workspace-eyebrow">{content.title}</span>
                        <h1>{content.title}</h1>
                        <p>{getModuleDescription(moduleKey, role)}</p>
                    </div>
                    <button type="button" className="primary-button" disabled>
                        {action}
                    </button>
                </section>
                <FilterBar filters={getModuleFilters(moduleKey, role)} />
                <section className="billing-placeholder">
                    <article className="billing-placeholder__panel">
                        <h2>Billing is intentionally parked for a later phase</h2>
                        <p>
                            We are keeping the entry point visible in the shell so the final product navigation feels complete,
                            but no pricing, invoice, or payment workflow is wired yet.
                        </p>
                    </article>
                    <article className="billing-placeholder__panel">
                        <h3>Planned capabilities</h3>
                        <ul>
                            <li>Consultation fee collection and payment status</li>
                            <li>Invoice and refund tracking tied to appointments</li>
                            <li>Reception and admin counter workflows</li>
                        </ul>
                    </article>
                </section>
                <ModuleApiPlan apis={content.apis} />
            </div>
        );
    }

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">{content.title}</span>
                    <h1>{content.title}</h1>
                    <p>{getModuleDescription(moduleKey, role)}</p>
                </div>
                <button type="button" className="primary-button">
                    {action}
                </button>
            </section>
            <FilterBar filters={getModuleFilters(moduleKey, role)} />
            <ModuleTable columns={content.columns} rows={rows} />
            <ModuleApiPlan apis={content.apis} />
        </div>
    );
}

function NotificationPanel({ notifications }) {
    return (
        <section className="notifications-panel" aria-label="Notifications">
            <div className="workspace-card__header">
                <div>
                    <span className="workspace-section-label">Notifications</span>
                    <h2>Recent updates</h2>
                </div>
            </div>
            <div className="notification-list">
                {notifications.map((notification) => (
                    <article className="notification-item" key={`${notification.title}-${notification.time}`}>
                        <div className="notification-item__dot" />
                        <div>
                            <strong>{notification.title}</strong>
                            <p>{notification.meta}</p>
                        </div>
                        <span>{notification.time}</span>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default function DashboardPage() {
    const navigate = useNavigate();
    const session = getSession();
    const role = session?.role || "STAFF";
    const navItems = getNavForRole(role);
    const [activeView, setActiveView] = useState(navItems[0]);
    const [notificationsOpen, setNotificationsOpen] = useState(false);

    useEffect(() => {
        if (!navItems.includes(activeView)) {
            setActiveView(navItems[0]);
        }
    }, [activeView, navItems]);

    if (!session) {
        return null;
    }

    const notifications = getNotificationsForRole(role);

    return (
        <main className="workspace-shell">
            <aside className="workspace-sidebar" aria-label="Main navigation">
                <div className="workspace-brand">
                    <div className="workspace-brand__mark">{session.hospitalName?.charAt(0) || "M"}</div>
                    <div>
                        <strong>{session.hospitalName || "MediTrack"}</strong>
                        <span>{getRoleLabel(role)} workspace</span>
                    </div>
                </div>

                <nav className="workspace-nav">
                    {navItems.map((item) => (
                        <button
                            type="button"
                            key={item}
                            className={activeView === item ? "is-active" : ""}
                            onClick={() => setActiveView(item)}
                        >
                            <span>{MODULE_DEFS[item].icon}</span>
                            {MODULE_DEFS[item].label}
                        </button>
                    ))}
                </nav>
            </aside>

            <section className="workspace-main">
                <header className="workspace-topbar">
                    <div>
                        <span className="workspace-topbar__eyebrow">
                            {session.hospitalCode} • {getRoleLabel(role)}
                        </span>
                        <h2>
                            {activeView === "dashboard"
                                ? `Welcome, ${session.fullName}`
                                : MODULE_DEFS[activeView].label}
                        </h2>
                    </div>

                    <div className="workspace-topbar__actions">
                        <div className="workspace-notifications">
                            <button
                                type="button"
                                className={`notification-button ${notificationsOpen ? "is-open" : ""}`}
                                onClick={() => setNotificationsOpen((current) => !current)}
                                aria-label="Open notifications"
                            >
                                <span className="notification-button__icon">N</span>
                                <span className="notification-button__count">{notifications.length}</span>
                            </button>
                            {notificationsOpen ? <NotificationPanel notifications={notifications} /> : null}
                        </div>

                        <div className="workspace-user">
                            <div className="workspace-user__avatar">{getInitials(session.fullName)}</div>
                            <div>
                                <strong>{session.fullName}</strong>
                                <span>{session.emailId}</span>
                            </div>
                            <button
                                type="button"
                                onClick={() => {
                                    clearSession();
                                    navigate(`/login/${session.hospitalCode}`, { replace: true });
                                }}
                            >
                                Sign out
                            </button>
                        </div>
                    </div>
                </header>

                {activeView === "dashboard" ? (
                    <DashboardView role={role} onOpenModule={setActiveView} />
                ) : (
                    <ListModuleView moduleKey={activeView} role={role} />
                )}
            </section>
        </main>
    );
}
