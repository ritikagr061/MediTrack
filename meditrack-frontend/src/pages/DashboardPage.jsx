import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { clearSession, getSession } from "../lib/authSession.js";
import {
    createAppointment,
    createDoctorSchedule,
    fetchAppointmentDoctors,
    fetchAppointments,
    fetchDoctorAvailability,
} from "../lib/appointmentApi.js";
import { createInvoice, createPayment, fetchInvoices } from "../lib/billingApi.js";
import { createEncounter, fetchEncounters, updateEncounterStatus } from "../lib/encounterApi.js";
import { createNotification, fetchNotifications } from "../lib/notificationApi.js";
import {
    createPatient,
    fetchPatientById,
    fetchPatientDiseases,
    fetchPatients,
    fetchPatientSummary,
} from "../lib/patientApi.js";
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
    reports: { label: "Notifications", icon: "N" },
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

const CREATE_PATIENT_ROLES = new Set(["ADMIN", "MANAGER", "RECEPTION", "STAFF"]);
const APPOINTMENT_WRITE_ROLES = new Set(["PATIENT", "ADMIN", "MANAGER", "RECEPTION", "STAFF"]);
const ENCOUNTER_WRITE_ROLES = new Set(["DOCTOR", "ADMIN"]);
const BILLING_WRITE_ROLES = new Set(["ADMIN", "MANAGER", "RECEPTION", "STAFF"]);
const NOTIFICATION_WRITE_ROLES = new Set(["ADMIN", "MANAGER", "RECEPTION", "STAFF"]);
const DEFAULT_ACTOR_ID = "00000000-0000-0000-0000-000000000001";

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
        intro: "Move quickly between your patients, today's appointments, open encounters, and reports that need action.",
        metrics: [
            { label: "Patients today", value: "18", detail: "12 consultations and 6 follow-ups" },
            { label: "Waiting now", value: "5", detail: "Average wait time 18 minutes" },
            { label: "Open encounters", value: "3", detail: "Need notes or treatment updates" },
            { label: "Reports pending", value: "4", detail: "Labs and imaging to review" },
        ],
        quickLinks: [
            { title: "My patients", detail: "Open the patient list for your assigned cases", target: "patients" },
            { title: "Today's appointments", detail: "Check arrivals, waitlist, and schedule", target: "appointments" },
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
            { title: "Support appointments", detail: "See today's room and visit flow", target: "appointments" },
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
            { title: "Appointment flow", detail: "Find bottlenecks in today's schedule", target: "appointments" },
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

const MODULE_CONTENT = {
    appointments: {
        title: "Appointments",
        descriptionByRole: {
            PATIENT: "Your bookings, reminders, and follow-up visits",
            DOCTOR: "Your schedule, queue, and visit flow",
            default: "Hospital appointment operations",
        },
        filtersByRole: {
            PATIENT: [
                { key: "status", label: "Status", options: ["All", "Confirmed", "Planned"] },
                { key: "type", label: "Visit type", options: ["All", "Follow-up", "Lab review"] },
                { key: "search", label: "Search", type: "search", placeholder: "Reason or clinician" },
            ],
            DOCTOR: [
                { key: "status", label: "Status", options: ["All", "Waiting", "Checked in", "Confirmed"] },
                { key: "type", label: "Visit type", options: ["All", "OPD", "Follow-up", "Teleconsult"] },
                { key: "search", label: "Search", type: "search", placeholder: "Patient or reason" },
            ],
            default: [
                { key: "status", label: "Status", options: ["All", "Checked in", "Cancelled", "Completed", "Planned"] },
                { key: "type", label: "Type", options: ["All", "ER", "OPD", "Teleconsult"] },
                { key: "search", label: "Search", type: "search", placeholder: "Patient or clinician" },
            ],
        },
        columns: ["Patient", "Type", "Reason", "Status", "Clinician", "Time", "Action"],
        rowsByRole: {
            PATIENT: [
                { patient: "You", type: "Follow-up", reason: "General consultation", status: { pill: "Confirmed", tone: "success" }, clinician: "Dr. Mehra", time: "May 11, 10:30 AM", action: "Open" },
                { patient: "You", type: "Lab review", reason: "Blood test discussion", status: { pill: "Planned", tone: "neutral" }, clinician: "Dr. Mehra", time: "May 14, 4:00 PM", action: "Open" },
            ],
            DOCTOR: [
                { patient: "Aarav Sharma", type: "OPD", reason: "Cough and fever", status: { pill: "Waiting", tone: "warning" }, clinician: "Dr. Mehra", time: "10:30 AM", action: "Open" },
                { patient: "Nisha Rao", type: "Follow-up", reason: "Medication review", status: { pill: "Checked in", tone: "info" }, clinician: "Dr. Mehra", time: "11:00 AM", action: "Open" },
                { patient: "Imran Khan", type: "Teleconsult", reason: "Lab report follow-up", status: { pill: "Confirmed", tone: "success" }, clinician: "Dr. Mehra", time: "12:15 PM", action: "Open" },
            ],
            default: [
                { patient: "John Doe", type: "ER", reason: "Chest pain", status: { pill: "Checked in", tone: "warning" }, clinician: "Dr. Sarah Nguyen", time: "Apr 22, 9:20 AM", action: "View" },
                { patient: "Mary Smith", type: "OPD", reason: "Routine check up", status: { pill: "Cancelled", tone: "neutral" }, clinician: "Dr. James Wilson", time: "Apr 20, 2:00 PM", action: "View" },
                { patient: "Robert Johnson", type: "Teleconsult", reason: "Pneumonia review", status: { pill: "Completed", tone: "success" }, clinician: "Dr. Emily Clark", time: "Apr 18, 4:15 PM", action: "View" },
                { patient: "Emma Wilson", type: "OPD", reason: "Skin rash", status: { pill: "Planned", tone: "info" }, clinician: "Dr. James Wilson", time: "Apr 15, 11:30 AM", action: "View" },
            ],
        },
        actionByRole: {
            PATIENT: "Book appointment",
            DOCTOR: "Open today's queue",
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
                { key: "status", label: "Status", options: ["All", "In progress", "Finished"] },
                { key: "type", label: "Type", options: ["All", "OPD", "Teleconsult"] },
                { key: "search", label: "Search", type: "search", placeholder: "Reason or doctor" },
            ],
            DOCTOR: [
                { key: "status", label: "Status", options: ["All", "In progress", "Draft", "Finished"] },
                { key: "type", label: "Type", options: ["All", "OPD", "Teleconsult"] },
                { key: "search", label: "Search", type: "search", placeholder: "Patient or reason" },
            ],
            default: [
                { key: "status", label: "Status", options: ["All", "In progress", "Cancelled", "Finished"] },
                { key: "type", label: "Type", options: ["All", "ER", "OPD", "IPD", "Teleconsult"] },
                { key: "search", label: "Search", type: "search", placeholder: "Patient or attending" },
            ],
        },
        columns: ["Patient", "Type", "Reason", "Status", "Attending", "Location", "Started", "Action"],
        rowsByRole: {
            PATIENT: [
                { patient: "You", type: "OPD", reason: "Respiratory follow-up", status: { pill: "In progress", tone: "warning" }, attending: "Dr. Mehra", location: "Clinic A, Room 2", started: "May 8, 2026", action: "Open" },
                { patient: "You", type: "Teleconsult", reason: "Lab report review", status: { pill: "Finished", tone: "success" }, attending: "Dr. Mehra", location: "Virtual", started: "Apr 28, 2026", action: "Open" },
            ],
            DOCTOR: [
                { patient: "Riya Kapoor", type: "OPD", reason: "Medication review", status: { pill: "In progress", tone: "warning" }, attending: "Dr. Mehra", location: "Clinic A, Room 2", started: "10:40 AM", action: "Open" },
                { patient: "Mohan Das", type: "Teleconsult", reason: "Follow-up plan", status: { pill: "Draft", tone: "info" }, attending: "Dr. Mehra", location: "Virtual", started: "9:25 AM", action: "Open" },
                { patient: "Anita Bose", type: "OPD", reason: "Dermatology review", status: { pill: "Finished", tone: "success" }, attending: "Dr. Mehra", location: "Clinic B", started: "Yesterday", action: "Open" },
            ],
            default: [
                { patient: "John Doe, 51 M", type: "ER", reason: "Chest pain", status: { pill: "In progress", tone: "warning" }, attending: "Dr. Sarah Nguyen", location: "Emergency Dept", started: "Apr 22, 2024", action: "View" },
                { patient: "Mary Smith, 56 F", type: "OPD", reason: "Routine check up", status: { pill: "Cancelled", tone: "neutral" }, attending: "Dr. James Wilson", location: "Clinic A (Room 2)", started: "Apr 20, 2024", action: "View" },
                { patient: "Robert Johnson, 63 M", type: "IPD", reason: "Pneumonia", status: { pill: "In progress", tone: "success" }, attending: "Dr. Emily Clark", location: "Ward 3", started: "Apr 18, 2024", action: "View" },
                { patient: "Alice Williams, 45 F", type: "Teleconsult", reason: "Follow-up visit", status: { pill: "Finished", tone: "neutral" }, attending: "Dr. Michael Brown", location: "Virtual", started: "Apr 15, 2024", action: "View" },
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
                { key: "status", label: "Status", options: ["All", "Ready"] },
                { key: "type", label: "Category", options: ["All", "Lab", "Medication", "Care plan"] },
                { key: "search", label: "Search", type: "search", placeholder: "Report or encounter" },
            ],
            DOCTOR: [
                { key: "status", label: "Status", options: ["All", "Needs review", "Reviewed", "Shared"] },
                { key: "type", label: "Category", options: ["All", "Lab", "Imaging", "Attachment"] },
                { key: "search", label: "Search", type: "search", placeholder: "Patient or report" },
            ],
            default: [
                { key: "status", label: "Status", options: ["All", "Ready", "Needs review", "Shared"] },
                { key: "type", label: "Category", options: ["All", "Export", "Imaging", "Attachment"] },
                { key: "search", label: "Search", type: "search", placeholder: "Report or owner" },
            ],
        },
        columns: ["Report", "Category", "Linked to", "Updated", "Status", "Action"],
        rowsByRole: {
            PATIENT: [
                { report: "CBC Report", category: "Lab", linkedTo: "Encounter #20041", updated: "May 8, 2026", status: { pill: "Ready", tone: "success" }, action: "Open" },
                { report: "Prescription", category: "Medication", linkedTo: "Encounter #20041", updated: "May 8, 2026", status: { pill: "Ready", tone: "success" }, action: "Open" },
                { report: "Discharge advice", category: "Care plan", linkedTo: "Encounter #19982", updated: "Apr 18, 2026", status: { pill: "Ready", tone: "neutral" }, action: "Open" },
            ],
            DOCTOR: [
                { report: "Radiology review", category: "Imaging", linkedTo: "Riya Kapoor", updated: "Today, 9:20 AM", status: { pill: "Needs review", tone: "warning" }, action: "Open" },
                { report: "CBC result", category: "Lab", linkedTo: "Imran Khan", updated: "Today, 8:50 AM", status: { pill: "Reviewed", tone: "success" }, action: "Open" },
                { report: "Discharge summary", category: "Attachment", linkedTo: "Mohan Das", updated: "Yesterday", status: { pill: "Shared", tone: "info" }, action: "Open" },
            ],
            default: [
                { report: "Encounter export", category: "Export", linkedTo: "Operational dashboard", updated: "Today, 11:30 AM", status: { pill: "Ready", tone: "success" }, action: "Download" },
                { report: "Pending imaging", category: "Imaging", linkedTo: "Dr. Sarah Nguyen", updated: "Today, 9:10 AM", status: { pill: "Needs review", tone: "warning" }, action: "Open" },
                { report: "Ward handoff summary", category: "Attachment", linkedTo: "Ward B", updated: "Yesterday", status: { pill: "Shared", tone: "info" }, action: "Open" },
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
        descriptionByRole: { default: "Planned workspace for invoicing, payments, and counter operations" },
        filtersByRole: {
            default: [
                { key: "state", label: "Status", options: ["Not yet implemented"] },
                { key: "scope", label: "Scope", options: ["Roadmap"] },
            ],
        },
        actionByRole: { default: "Billing is YNI" },
        apis: ["POST /billing/invoices", "GET /billing/transactions", "PATCH /billing/payments/{id}"],
    },
};

const EMPTY_PATIENT_FORM = {
    name: "",
    address: "",
    phone: "",
    email: "",
    aadhar: "",
    pan: "",
    dateOfBirth: "",
    gender: "",
};

const EMPTY_APPOINTMENT_FORM = {
    patientId: "",
    doctorId: "",
    appointmentType: "OPD",
    reasonText: "",
    notes: "",
    startsAt: "",
    durationMinutes: "30",
};

const EMPTY_SCHEDULE_FORM = {
    doctorId: "",
    dayOfWeek: "MONDAY",
    startTime: "09:00",
    endTime: "17:00",
    slotDurationMinutes: "30",
    bufferMinutes: "0",
    consultationFee: "",
};

const EMPTY_ENCOUNTER_FORM = {
    patientId: "",
    appointmentId: "",
    attendingDoctorId: "",
    encounterType: "OPD",
    chiefComplaint: "",
    reasonText: "",
    locationType: "OPD_ROOM",
    locationText: "",
};

const EMPTY_INVOICE_FORM = {
    patientId: "",
    invoiceType: "CONSULTATION",
    itemName: "Consultation fee",
    itemType: "CONSULTATION_FEE",
    quantity: "1",
    unitAmount: "500",
    discountAmount: "0",
    taxAmount: "0",
};

const EMPTY_NOTIFICATION_FORM = {
    patientId: "",
    channel: "EMAIL",
    recipientAddress: "",
    recipientName: "",
    subject: "",
    body: "",
};

function getRoleLabel(role) {
    return ROLE_LABELS[role] || "Staff";
}

function getInitials(name = "") {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (!parts.length) return "MT";
    return parts.slice(0, 2).map((part) => part[0].toUpperCase()).join("");
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
    return content?.descriptionByRole?.[role] || content?.descriptionByRole?.default || "";
}

function getModuleFilters(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    return content?.filtersByRole?.[role] || content?.filtersByRole?.default || [];
}

function getModuleRows(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    return content?.rowsByRole?.[role] || content?.rowsByRole?.default || [];
}

function getModuleAction(moduleKey, role) {
    const content = MODULE_CONTENT[moduleKey];
    return content?.actionByRole?.[role] || content?.actionByRole?.default || "";
}

function rowMatchesFilters(row, filters) {
    return Object.entries(filters).every(([key, value]) => {
        if (!value || value === "All") return true;
        if (key === "search") {
            const haystack = Object.values(row)
                .map((entry) => (entry && typeof entry === "object" && "pill" in entry ? entry.pill : String(entry)))
                .join(" ")
                .toLowerCase();
            return haystack.includes(value.toLowerCase());
        }

        const rowValue = row[key];
        const normalized = rowValue && typeof rowValue === "object" && "pill" in rowValue ? rowValue.pill : String(rowValue);
        return normalized.toLowerCase() === String(value).toLowerCase();
    });
}

function formatDateOfBirth(value) {
    if (!value) return "Not provided";
    return new Date(value).toLocaleDateString("en-IN", { year: "numeric", month: "short", day: "numeric" });
}

function formatDateTime(value) {
    if (!value) return "Not set";
    return new Date(value).toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        hour: "2-digit",
        minute: "2-digit",
    });
}

function toIsoDateTime(value) {
    if (!value) return null;
    return new Date(value).toISOString();
}

function getBookedByRole(role) {
    if (role === "PATIENT" || role === "DOCTOR") return role;
    if (role === "RECEPTION" || role === "STAFF") return "RECEPTIONIST";
    return "ADMIN";
}

function getStatusTone(status) {
    const normalized = String(status || "").toUpperCase();
    if (["CONFIRMED", "FINISHED", "COMPLETED", "PAID", "SENT", "DELIVERED"].includes(normalized)) return "success";
    if (["IN_PROGRESS", "CHECKED_IN", "PENDING", "PLANNED", "SCHEDULED"].includes(normalized)) return "info";
    if (["CANCELLED", "FAILED", "VOID"].includes(normalized)) return "neutral";
    return "warning";
}

function getPageContent(page) {
    return page?.content || (Array.isArray(page) ? page : []);
}

function makeNameMap(items) {
    return new Map(items.map((item) => [item.id, item.name || item.patientCode || item.email || item.id]));
}

function buildPatientOptions(patients) {
    return patients.map((patient) => ({
        id: patient.id,
        label: `${patient.name}${patient.patientCode ? ` (${patient.patientCode})` : ""}`,
        email: patient.email,
    }));
}

function getActiveValue(entity) {
    if (typeof entity?.isActive === "boolean") return entity.isActive;
    if (typeof entity?.active === "boolean") return entity.active;
    return true;
}

function resolveColumnKey(column) {
    const lookup = {
        Patient: "patient",
        PatientCode: "patientCode",
        PrimaryNeed: "primaryneed",
        Assigned: "assigned",
        LastVisit: "lastvisit",
        Status: "status",
        Action: "action",
        Type: "type",
        Reason: "reason",
        Clinician: "clinician",
        Time: "time",
        Attending: "attending",
        Location: "location",
        Started: "started",
        Report: "report",
        Category: "category",
        LinkedTo: "linkedTo",
        Updated: "updated",
        Invoice: "invoice",
        Total: "total",
        Due: "due",
        Recipient: "recipient",
        Channel: "channel",
        Subject: "subject",
        Source: "source",
        Sent: "sent",
    };

    return lookup[column] || column.toLowerCase().replaceAll(" ", "");
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
                    <button key={link.title} type="button" className="shortcut-card" onClick={() => onOpenModule(link.target)}>
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
                {spotlight.items.map((item) => <li key={item}>{item}</li>)}
            </ul>
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

function FilterBar({ filters, values, onChange }) {
    return (
        <section className="filter-bar" aria-label="Filters">
            {filters.map((filter) => (
                <label className={`filter-pill filter-pill--${filter.key}`} key={filter.key}>
                    <span>{filter.label}</span>
                    {filter.type === "search" ? (
                        <input
                            className="filter-input"
                            type="search"
                            placeholder={filter.placeholder || "Search"}
                            value={values[filter.key] || ""}
                            onChange={(event) => onChange(filter.key, event.target.value)}
                        />
                    ) : (
                        <select className="filter-select" value={values[filter.key] || filter.options[0]} onChange={(event) => onChange(filter.key, event.target.value)}>
                            {filter.options.map((option) => (
                                <option value={option} key={option}>{option}</option>
                            ))}
                        </select>
                    )}
                </label>
            ))}
        </section>
    );
}

function ModuleTable({ columns, rows, onAction, emptyState }) {
    return (
        <div className="table-shell">
            <table className="workspace-table">
                <thead>
                    <tr>
                        {columns.map((column) => <th key={column}>{column}</th>)}
                    </tr>
                </thead>
                <tbody>
                    {rows.length ? rows.map((row, index) => (
                        <tr key={`${index}-${row.action || row.patient || row.report}`}>
                            {columns.map((column) => {
                                const key = resolveColumnKey(column);
                                const value = row[key] ?? row[column];
                                if (key === "action") {
                                    return (
                                        <td key={column}>
                                            <button type="button" className="table-action" onClick={() => onAction?.(row)}>
                                                {value}
                                            </button>
                                        </td>
                                    );
                                }
                                return (
                                    <td key={column}>
                                        {value && typeof value === "object" && "pill" in value ? (
                                            <Pill value={value.pill} tone={value.tone} />
                                        ) : (
                                            <span>{value}</span>
                                        )}
                                    </td>
                                );
                            })}
                        </tr>
                    )) : (
                        <tr>
                            <td colSpan={columns.length} className="workspace-table__empty">{emptyState}</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
}

function ModuleApiPlan() {
    return null;
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

function PatientDrawer({ detail, loading, onClose }) {
    return (
        <aside className={`workspace-drawer ${detail || loading ? "is-open" : ""}`} aria-hidden={!detail && !loading}>
            <div className="workspace-drawer__card">
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Patient details</span>
                        <h2>{detail?.patient?.name || "Loading patient..."}</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>

                {loading ? (
                    <div className="workspace-card__state">Loading patient context...</div>
                ) : detail ? (
                    <div className="drawer-stack">
                        <section className="drawer-grid">
                            <article className="drawer-panel">
                                <h3>Profile</h3>
                                <dl>
                                    <div><dt>Patient code</dt><dd>{detail.patient.patientCode || "Pending"}</dd></div>
                                    <div><dt>Email</dt><dd>{detail.patient.email || "Not provided"}</dd></div>
                                    <div><dt>Phone</dt><dd>{detail.patient.phone || "Not provided"}</dd></div>
                                    <div><dt>Date of birth</dt><dd>{formatDateOfBirth(detail.patient.dateOfBirth)}</dd></div>
                                    <div><dt>Gender</dt><dd>{detail.patient.gender || "Not provided"}</dd></div>
                                    <div><dt>Address</dt><dd>{detail.patient.address || "Not provided"}</dd></div>
                                </dl>
                            </article>
                            <article className="drawer-panel">
                                <h3>Summary</h3>
                                <dl>
                                    <div><dt>Status</dt><dd>{getActiveValue(detail.patient) ? "Active" : "Inactive"}</dd></div>
                                    <div><dt>Disease count</dt><dd>{detail.summary?.diseaseCount ?? 0}</dd></div>
                                    <div><dt>Appointments</dt><dd>{detail.summary?.appointmentCount ?? 0}</dd></div>
                                    <div><dt>Encounters</dt><dd>{detail.summary?.encounterCount ?? 0}</dd></div>
                                </dl>
                            </article>
                        </section>

                        <section className="drawer-panel">
                            <h3>Disease history</h3>
                            {detail.diseases?.length ? (
                                <div className="drawer-list">
                                    {detail.diseases.map((disease) => (
                                        <article key={disease.id} className="drawer-list__item">
                                            <div>
                                                <strong>{disease.diseaseName}</strong>
                                                <p>{disease.notes || "No notes recorded yet."}</p>
                                            </div>
                                            <div className="drawer-list__meta">
                                                <Pill value={disease.isChronic ? "Chronic" : "Recorded"} tone={disease.isChronic ? "warning" : "info"} />
                                                <span>{disease.diagnosedAt || "Date not set"}</span>
                                            </div>
                                        </article>
                                    ))}
                                </div>
                            ) : (
                                <div className="workspace-card__state">No disease history has been added yet.</div>
                            )}
                        </section>
                    </div>
                ) : (
                    <div className="workspace-card__state">Select a patient from the list to view more details.</div>
                )}
            </div>
        </aside>
    );
}

function PatientCreateModal({ open, form, onChange, onClose, onSubmit, submitting, error }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card" role="dialog" aria-modal="true" aria-label="Add patient" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Patients</span>
                        <h2>Add patient</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>

                <form className="patient-form" onSubmit={onSubmit}>
                    <label><span>Name</span><input required value={form.name} onChange={(event) => onChange("name", event.target.value)} /></label>
                    <label><span>Phone</span><input required value={form.phone} onChange={(event) => onChange("phone", event.target.value)} /></label>
                    <label><span>Email</span><input required type="email" value={form.email} onChange={(event) => onChange("email", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Address</span><textarea required rows="3" value={form.address} onChange={(event) => onChange("address", event.target.value)} /></label>
                    <label><span>Aadhar</span><input value={form.aadhar} onChange={(event) => onChange("aadhar", event.target.value)} /></label>
                    <label><span>PAN</span><input value={form.pan} onChange={(event) => onChange("pan", event.target.value)} /></label>
                    <label><span>Date of birth</span><input type="date" value={form.dateOfBirth} onChange={(event) => onChange("dateOfBirth", event.target.value)} /></label>
                    <label>
                        <span>Gender</span>
                        <select value={form.gender} onChange={(event) => onChange("gender", event.target.value)}>
                            <option value="">Select</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </label>

                    {error ? <div className="login-form__error">{error}</div> : null}

                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>
                            {submitting ? "Creating..." : "Create patient"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function PatientsModule({ role, session }) {
    const [filters, setFilters] = useState({ search: "", isActive: "Active" });
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [selectedPatientId, setSelectedPatientId] = useState(null);
    const [selectedPatientDetail, setSelectedPatientDetail] = useState(null);
    const [detailLoading, setDetailLoading] = useState(false);
    const [refreshIndex, setRefreshIndex] = useState(0);
    const [createOpen, setCreateOpen] = useState(false);
    const [createForm, setCreateForm] = useState(EMPTY_PATIENT_FORM);
    const [createError, setCreateError] = useState("");
    const [creating, setCreating] = useState(false);

    useEffect(() => {
        let ignore = false;

        async function loadPatients() {
            setLoading(true);
            setError("");
            try {
                const result = await fetchPatients({
                    hospitalId: session.hospitalId,
                    search: filters.search,
                    isActive: filters.isActive === "All" ? undefined : filters.isActive === "Active",
                    page: 0,
                    size: 20,
                });

                if (!ignore) {
                    setPatients(result?.content || []);
                }
            } catch (loadError) {
                if (!ignore) {
                    setError(loadError.message || "Unable to load patients.");
                }
            } finally {
                if (!ignore) {
                    setLoading(false);
                }
            }
        }

        loadPatients();
        return () => { ignore = true; };
    }, [filters.search, filters.isActive, refreshIndex, session.hospitalId]);

    useEffect(() => {
        if (!selectedPatientId) return undefined;
        let ignore = false;

        async function loadDetail() {
            setDetailLoading(true);
            try {
                const [patient, summary, diseases] = await Promise.all([
                    fetchPatientById(selectedPatientId),
                    fetchPatientSummary(selectedPatientId),
                    fetchPatientDiseases(selectedPatientId),
                ]);

                if (!ignore) {
                    setSelectedPatientDetail({ patient, summary, diseases });
                }
            } catch (loadError) {
                if (!ignore) {
                    setSelectedPatientDetail({
                        patient: { name: "Unable to load patient" },
                        summary: null,
                        diseases: [],
                        error: loadError.message,
                    });
                }
            } finally {
                if (!ignore) {
                    setDetailLoading(false);
                }
            }
        }

        loadDetail();
        return () => { ignore = true; };
    }, [selectedPatientId]);

    const rows = patients.map((patient) => {
        const patientIsActive = getActiveValue(patient);
        return {
            patient: patient.name,
            patientCode: patient.patientCode || "Pending",
            primaryneed: patient.email || patient.phone,
            assigned: role === "DOCTOR" ? "My list" : session.hospitalName,
            lastvisit: patient.updatedAt ? new Date(patient.updatedAt).toLocaleDateString("en-IN") : "Not updated",
            status: { pill: patientIsActive ? "Active" : "Inactive", tone: patientIsActive ? "success" : "neutral" },
            action: "Open",
            __id: patient.id,
        };
    });

    const columns = ["Patient", "PatientCode", "PrimaryNeed", "Assigned", "LastVisit", "Status", "Action"];

    const onCreateSubmit = async (event) => {
        event.preventDefault();
        setCreateError("");
        setCreating(true);
        try {
            await createPatient({
                hospitalId: session.hospitalId,
                name: createForm.name,
                address: createForm.address,
                phone: createForm.phone,
                email: createForm.email,
                aadhar: createForm.aadhar || null,
                pan: createForm.pan || null,
                dateOfBirth: createForm.dateOfBirth || null,
                gender: createForm.gender || null,
            });

            setCreateForm(EMPTY_PATIENT_FORM);
            setCreateOpen(false);
            setRefreshIndex((current) => current + 1);
        } catch (submitError) {
            setCreateError(submitError.message || "Unable to create patient.");
        } finally {
            setCreating(false);
        }
    };

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">Patients</span>
                    <h1>Patients</h1>
                    <p>{role === "DOCTOR" ? "Patient directory using the current hospital patient APIs until doctor-specific ownership APIs are added." : "Live patient list from patient-service with search and status filters."}</p>
                </div>
                <div className="module-heading__actions">
                    <button type="button" className="secondary-button" onClick={() => setRefreshIndex((current) => current + 1)}>
                        Refresh
                    </button>
                    {CREATE_PATIENT_ROLES.has(role) ? (
                        <button type="button" className="primary-button" onClick={() => setCreateOpen(true)}>
                            Add patient
                        </button>
                    ) : null}
                </div>
            </section>

            <FilterBar
                filters={[
                    { key: "search", label: "Search", type: "search", placeholder: "Name, phone, email or patient code" },
                    { key: "isActive", label: "Status", options: ["All", "Active", "Inactive"] },
                ]}
                values={filters}
                onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))}
            />

            {error ? <div className="workspace-card__state workspace-card__state--error">{error}</div> : null}

            {loading ? (
                <div className="workspace-card__state">Loading patients from patient-service...</div>
            ) : (
                <ModuleTable
                    columns={columns}
                    rows={rows}
                    emptyState="No patients matched the current filters."
                    onAction={(row) => {
                        setSelectedPatientId(row.__id);
                        setSelectedPatientDetail(null);
                    }}
                />
            )}

            <ModuleApiPlan apis={["GET /patients", "GET /patients/{id}", "GET /patients/{id}/summary", "GET /patients/{id}/diseases", "POST /patients"]} />

            <PatientDrawer
                loading={detailLoading}
                detail={selectedPatientDetail}
                onClose={() => {
                    setSelectedPatientId(null);
                    setSelectedPatientDetail(null);
                }}
            />

            <PatientCreateModal
                open={createOpen}
                form={createForm}
                onChange={(key, value) => setCreateForm((current) => ({ ...current, [key]: value }))}
                onClose={() => {
                    setCreateOpen(false);
                    setCreateError("");
                }}
                onSubmit={onCreateSubmit}
                submitting={creating}
                error={createError}
            />
        </div>
    );
}

function AppointmentCreateModal({ open, form, patients, doctors, slots, checkingSlots, error, submitting, onChange, onClose, onSubmit, onCheckSlots }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card modal-card--wide" role="dialog" aria-modal="true" aria-label="Book appointment" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Appointments</span>
                        <h2>Book appointment</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>

                <form className="patient-form" onSubmit={onSubmit}>
                    <label>
                        <span>Patient</span>
                        <select required value={form.patientId} onChange={(event) => onChange("patientId", event.target.value)}>
                            <option value="">Select patient</option>
                            {patients.map((patient) => <option value={patient.id} key={patient.id}>{patient.label}</option>)}
                        </select>
                    </label>
                    <label>
                        <span>Doctor</span>
                        <select required value={form.doctorId} onChange={(event) => onChange("doctorId", event.target.value)}>
                            <option value="">Select doctor</option>
                            {doctors.map((doctor) => <option value={doctor.id} key={doctor.id}>{doctor.name} - {doctor.specialty || "General"}</option>)}
                        </select>
                    </label>
                    <label>
                        <span>Type</span>
                        <select value={form.appointmentType} onChange={(event) => onChange("appointmentType", event.target.value)}>
                            <option value="OPD">OPD</option>
                            <option value="TELECONSULT">Teleconsult</option>
                            <option value="PROCEDURE">Procedure</option>
                        </select>
                    </label>
                    <label>
                        <span>Start time</span>
                        <input required type="datetime-local" value={form.startsAt} onChange={(event) => onChange("startsAt", event.target.value)} />
                    </label>
                    <label>
                        <span>Duration minutes</span>
                        <input required min="1" type="number" value={form.durationMinutes} onChange={(event) => onChange("durationMinutes", event.target.value)} />
                    </label>
                    <label className="patient-form__full"><span>Reason</span><textarea required rows="3" value={form.reasonText} onChange={(event) => onChange("reasonText", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Notes</span><textarea rows="2" value={form.notes} onChange={(event) => onChange("notes", event.target.value)} /></label>

                    <div className="patient-form__full form-note">
                        <button type="button" className="secondary-button" onClick={onCheckSlots} disabled={!form.doctorId || !form.startsAt || checkingSlots}>
                            {checkingSlots ? "Checking..." : "Check doctor slots"}
                        </button>
                        {slots.length ? (
                            <div className="slot-list">
                                {slots.slice(0, 6).map((slot) => <span key={slot.startsAt || slot.startTime}>{formatDateTime(slot.startsAt || slot.startTime)}</span>)}
                            </div>
                        ) : null}
                    </div>

                    {error ? <div className="login-form__error">{error}</div> : null}

                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Booking..." : "Book appointment"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function DoctorScheduleModal({ open, form, doctors, error, submitting, onChange, onClose, onSubmit }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card" role="dialog" aria-modal="true" aria-label="Add doctor schedule" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Doctor schedule</span>
                        <h2>Add schedule</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>
                <form className="patient-form" onSubmit={onSubmit}>
                    <label className="patient-form__full">
                        <span>Doctor</span>
                        <select required value={form.doctorId} onChange={(event) => onChange("doctorId", event.target.value)}>
                            <option value="">Select doctor</option>
                            {doctors.map((doctor) => <option value={doctor.id} key={doctor.id}>{doctor.name}</option>)}
                        </select>
                    </label>
                    <label><span>Day</span><select value={form.dayOfWeek} onChange={(event) => onChange("dayOfWeek", event.target.value)}>
                        {["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"].map((day) => <option key={day} value={day}>{day}</option>)}
                    </select></label>
                    <label><span>Start</span><input required type="time" value={form.startTime} onChange={(event) => onChange("startTime", event.target.value)} /></label>
                    <label><span>End</span><input required type="time" value={form.endTime} onChange={(event) => onChange("endTime", event.target.value)} /></label>
                    <label><span>Slot minutes</span><input required type="number" min="1" value={form.slotDurationMinutes} onChange={(event) => onChange("slotDurationMinutes", event.target.value)} /></label>
                    <label><span>Buffer minutes</span><input required type="number" min="0" value={form.bufferMinutes} onChange={(event) => onChange("bufferMinutes", event.target.value)} /></label>
                    <label><span>Fee</span><input type="number" min="0" value={form.consultationFee} onChange={(event) => onChange("consultationFee", event.target.value)} /></label>
                    {error ? <div className="login-form__error">{error}</div> : null}
                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Saving..." : "Save schedule"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function AppointmentsModule({ role, session }) {
    const [filters, setFilters] = useState({ status: "All", search: "" });
    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshIndex, setRefreshIndex] = useState(0);
    const [createOpen, setCreateOpen] = useState(false);
    const [scheduleOpen, setScheduleOpen] = useState(false);
    const [form, setForm] = useState(EMPTY_APPOINTMENT_FORM);
    const [scheduleForm, setScheduleForm] = useState(EMPTY_SCHEDULE_FORM);
    const [submitError, setSubmitError] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [slots, setSlots] = useState([]);
    const [checkingSlots, setCheckingSlots] = useState(false);

    useEffect(() => {
        let ignore = false;
        async function loadData() {
            setLoading(true);
            setError("");
            try {
                const [appointmentPage, patientPage, doctorPage] = await Promise.all([
                    fetchAppointments({
                        hospitalId: session.hospitalId,
                        status: filters.status === "All" ? undefined : filters.status,
                        page: 0,
                        size: 30,
                    }),
                    fetchPatients({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                    fetchAppointmentDoctors({ hospitalId: session.hospitalId, search: filters.search, page: 0, size: 50 }),
                ]);

                if (!ignore) {
                    setAppointments(getPageContent(appointmentPage));
                    setPatients(getPageContent(patientPage));
                    setDoctors(getPageContent(doctorPage));
                }
            } catch (loadError) {
                if (!ignore) setError(loadError.message || "Unable to load appointments.");
            } finally {
                if (!ignore) setLoading(false);
            }
        }

        loadData();
        return () => { ignore = true; };
    }, [filters.status, filters.search, refreshIndex, session.hospitalId]);

    const patientNameMap = makeNameMap(patients);
    const doctorNameMap = makeNameMap(doctors);
    const rows = appointments.map((appointment) => ({
        patient: patientNameMap.get(appointment.patientId) || appointment.patientId,
        type: appointment.appointmentType,
        reason: appointment.reasonText,
        status: { pill: appointment.status, tone: getStatusTone(appointment.status) },
        clinician: doctorNameMap.get(appointment.doctorId) || appointment.doctorId,
        time: formatDateTime(appointment.startsAt),
        action: "Open",
    }));

    const onCreateSubmit = async (event) => {
        event.preventDefault();
        setSubmitError("");
        setSubmitting(true);
        try {
            await createAppointment({
                hospitalId: session.hospitalId,
                patientId: form.patientId,
                doctorId: form.doctorId,
                appointmentType: form.appointmentType,
                reasonText: form.reasonText,
                notes: form.notes || null,
                startsAt: toIsoDateTime(form.startsAt),
                durationMinutes: Number(form.durationMinutes),
                bookedByUserId: DEFAULT_ACTOR_ID,
                bookedByRole: getBookedByRole(role),
            });
            setForm(EMPTY_APPOINTMENT_FORM);
            setSlots([]);
            setCreateOpen(false);
            setRefreshIndex((current) => current + 1);
        } catch (submitErrorValue) {
            setSubmitError(submitErrorValue.message || "Unable to book appointment.");
        } finally {
            setSubmitting(false);
        }
    };

    const onScheduleSubmit = async (event) => {
        event.preventDefault();
        setSubmitError("");
        setSubmitting(true);
        try {
            await createDoctorSchedule({
                hospitalId: session.hospitalId,
                doctorId: scheduleForm.doctorId,
                dayOfWeek: scheduleForm.dayOfWeek,
                startTime: scheduleForm.startTime,
                endTime: scheduleForm.endTime,
                slotDurationMinutes: Number(scheduleForm.slotDurationMinutes),
                bufferMinutes: Number(scheduleForm.bufferMinutes),
                consultationFee: scheduleForm.consultationFee ? Number(scheduleForm.consultationFee) : null,
            });
            setScheduleForm(EMPTY_SCHEDULE_FORM);
            setScheduleOpen(false);
        } catch (submitErrorValue) {
            setSubmitError(submitErrorValue.message || "Unable to save schedule.");
        } finally {
            setSubmitting(false);
        }
    };

    const onCheckSlots = async () => {
        setSubmitError("");
        setCheckingSlots(true);
        try {
            const result = await fetchDoctorAvailability({
                hospitalId: session.hospitalId,
                doctorId: form.doctorId,
                date: form.startsAt.slice(0, 10),
            });
            setSlots(result || []);
        } catch (slotError) {
            setSubmitError(slotError.message || "Unable to check slots.");
        } finally {
            setCheckingSlots(false);
        }
    };

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">Appointments</span>
                    <h1>Appointments</h1>
                    <p>Book appointments only when the doctor has schedule availability and no clash exists.</p>
                </div>
                <div className="module-heading__actions">
                    <button type="button" className="secondary-button" onClick={() => setRefreshIndex((current) => current + 1)}>Refresh</button>
                    {role !== "PATIENT" ? <button type="button" className="secondary-button" onClick={() => { setSubmitError(""); setScheduleOpen(true); }}>Add schedule</button> : null}
                    {APPOINTMENT_WRITE_ROLES.has(role) ? <button type="button" className="primary-button" onClick={() => { setSubmitError(""); setCreateOpen(true); }}>Book appointment</button> : null}
                </div>
            </section>

            <FilterBar
                filters={[
                    { key: "status", label: "Status", options: ["All", "REQUESTED", "CONFIRMED", "CHECKED_IN", "COMPLETED", "CANCELLED", "NO_SHOW"] },
                    { key: "search", label: "Doctor search", type: "search", placeholder: "Doctor name or specialty" },
                ]}
                values={filters}
                onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))}
            />

            {error ? <div className="workspace-card__state workspace-card__state--error">{error}</div> : null}
            {loading ? <div className="workspace-card__state">Loading appointments...</div> : (
                <ModuleTable columns={["Patient", "Type", "Reason", "Status", "Clinician", "Time", "Action"]} rows={rows} emptyState="No appointments found." />
            )}
            <ModuleApiPlan apis={["GET /appointments", "POST /appointments", "GET /appointments/doctors", "GET /appointments/availability", "POST /appointments/doctor-schedules"]} />

            <AppointmentCreateModal
                open={createOpen}
                form={form}
                patients={buildPatientOptions(patients)}
                doctors={doctors}
                slots={slots}
                checkingSlots={checkingSlots}
                error={submitError}
                submitting={submitting}
                onChange={(key, value) => setForm((current) => ({ ...current, [key]: value }))}
                onClose={() => { setCreateOpen(false); setSubmitError(""); }}
                onSubmit={onCreateSubmit}
                onCheckSlots={onCheckSlots}
            />
            <DoctorScheduleModal
                open={scheduleOpen}
                form={scheduleForm}
                doctors={doctors}
                error={submitError}
                submitting={submitting}
                onChange={(key, value) => setScheduleForm((current) => ({ ...current, [key]: value }))}
                onClose={() => { setScheduleOpen(false); setSubmitError(""); }}
                onSubmit={onScheduleSubmit}
            />
        </div>
    );
}

function EncounterCreateModal({ open, form, patients, doctors, appointments, error, submitting, onChange, onClose, onSubmit }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card modal-card--wide" role="dialog" aria-modal="true" aria-label="Start encounter" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Encounters</span>
                        <h2>Start encounter</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>
                <form className="patient-form" onSubmit={onSubmit}>
                    <label><span>Patient</span><select required value={form.patientId} onChange={(event) => onChange("patientId", event.target.value)}>
                        <option value="">Select patient</option>
                        {patients.map((patient) => <option value={patient.id} key={patient.id}>{patient.label}</option>)}
                    </select></label>
                    <label><span>Appointment</span><select value={form.appointmentId} onChange={(event) => onChange("appointmentId", event.target.value)}>
                        <option value="">No appointment</option>
                        {appointments.map((appointment) => <option value={appointment.id} key={appointment.id}>{appointment.appointmentCode || formatDateTime(appointment.startsAt)}</option>)}
                    </select></label>
                    <label><span>Doctor</span><select required value={form.attendingDoctorId} onChange={(event) => onChange("attendingDoctorId", event.target.value)}>
                        <option value="">Select doctor</option>
                        {doctors.map((doctor) => <option value={doctor.id} key={doctor.id}>{doctor.name}</option>)}
                    </select></label>
                    <label><span>Type</span><select value={form.encounterType} onChange={(event) => onChange("encounterType", event.target.value)}>
                        <option value="OPD">OPD</option>
                        <option value="IPD">IPD</option>
                        <option value="ER">ER</option>
                        <option value="TELECONSULT">Teleconsult</option>
                    </select></label>
                    <label><span>Location type</span><select value={form.locationType} onChange={(event) => onChange("locationType", event.target.value)}>
                        <option value="OPD_ROOM">OPD room</option>
                        <option value="WARD">Ward</option>
                        <option value="BED">Bed</option>
                        <option value="ER">ER</option>
                        <option value="VIRTUAL">Virtual</option>
                    </select></label>
                    <label><span>Location</span><input value={form.locationText} onChange={(event) => onChange("locationText", event.target.value)} placeholder="OPD Room 5" /></label>
                    <label className="patient-form__full"><span>Chief complaint</span><textarea required rows="3" value={form.chiefComplaint} onChange={(event) => onChange("chiefComplaint", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Reason notes</span><textarea rows="2" value={form.reasonText} onChange={(event) => onChange("reasonText", event.target.value)} /></label>
                    {error ? <div className="login-form__error">{error}</div> : null}
                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Starting..." : "Start encounter"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function EncountersModule({ role, session }) {
    const [filters, setFilters] = useState({ status: "All", type: "All" });
    const [encounters, setEncounters] = useState([]);
    const [appointments, setAppointments] = useState([]);
    const [patients, setPatients] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshIndex, setRefreshIndex] = useState(0);
    const [createOpen, setCreateOpen] = useState(false);
    const [form, setForm] = useState(EMPTY_ENCOUNTER_FORM);
    const [submitError, setSubmitError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        let ignore = false;
        async function loadData() {
            setLoading(true);
            setError("");
            try {
                const [encounterPage, appointmentPage, patientPage, doctorPage] = await Promise.all([
                    fetchEncounters({
                        hospitalId: session.hospitalId,
                        status: filters.status === "All" ? undefined : filters.status,
                        encounterType: filters.type === "All" ? undefined : filters.type,
                        page: 0,
                        size: 30,
                    }),
                    fetchAppointments({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                    fetchPatients({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                    fetchAppointmentDoctors({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                ]);
                if (!ignore) {
                    setEncounters(getPageContent(encounterPage));
                    setAppointments(getPageContent(appointmentPage));
                    setPatients(getPageContent(patientPage));
                    setDoctors(getPageContent(doctorPage));
                }
            } catch (loadError) {
                if (!ignore) setError(loadError.message || "Unable to load encounters.");
            } finally {
                if (!ignore) setLoading(false);
            }
        }

        loadData();
        return () => { ignore = true; };
    }, [filters.status, filters.type, refreshIndex, session.hospitalId]);

    const patientNameMap = makeNameMap(patients);
    const doctorNameMap = makeNameMap(doctors);
    const rows = encounters.map((encounter) => ({
        patient: patientNameMap.get(encounter.patientId) || encounter.patientId,
        type: encounter.encounterType,
        reason: encounter.chiefComplaint,
        status: { pill: encounter.status, tone: getStatusTone(encounter.status) },
        attending: doctorNameMap.get(encounter.attendingDoctorId) || encounter.attendingDoctorId,
        location: encounter.locationText || encounter.locationType || "Not set",
        started: formatDateTime(encounter.startedAt || encounter.createdAt),
        action: encounter.status === "FINISHED" || encounter.status === "CANCELLED" ? "Open" : "Finish",
        __id: encounter.id,
    }));

    const onCreateSubmit = async (event) => {
        event.preventDefault();
        setSubmitError("");
        setSubmitting(true);
        try {
            await createEncounter({
                hospitalId: session.hospitalId,
                patientId: form.patientId,
                appointmentId: form.appointmentId || null,
                attendingDoctorId: form.attendingDoctorId,
                createdByDoctorId: form.attendingDoctorId,
                encounterType: form.encounterType,
                chiefComplaint: form.chiefComplaint,
                reasonText: form.reasonText || null,
                locationType: form.locationType || null,
                locationText: form.locationText || null,
                startedAt: new Date().toISOString(),
            });
            setForm(EMPTY_ENCOUNTER_FORM);
            setCreateOpen(false);
            setRefreshIndex((current) => current + 1);
        } catch (submitErrorValue) {
            setSubmitError(submitErrorValue.message || "Unable to start encounter.");
        } finally {
            setSubmitting(false);
        }
    };

    const onRowAction = async (row) => {
        if (row.action !== "Finish") return;
        setError("");
        try {
            await updateEncounterStatus(row.__id, "FINISHED");
            setRefreshIndex((current) => current + 1);
        } catch (actionError) {
            setError(actionError.message || "Unable to finish encounter.");
        }
    };

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">Encounters</span>
                    <h1>Encounters</h1>
                    <p>Doctors can create an encounter when they meet the patient for an appointment or visit.</p>
                </div>
                <div className="module-heading__actions">
                    <button type="button" className="secondary-button" onClick={() => setRefreshIndex((current) => current + 1)}>Refresh</button>
                    {ENCOUNTER_WRITE_ROLES.has(role) ? <button type="button" className="primary-button" onClick={() => { setSubmitError(""); setCreateOpen(true); }}>Start encounter</button> : null}
                </div>
            </section>

            <FilterBar
                filters={[
                    { key: "status", label: "Status", options: ["All", "PLANNED", "IN_PROGRESS", "ON_HOLD", "FINISHED", "CANCELLED"] },
                    { key: "type", label: "Type", options: ["All", "OPD", "IPD", "ER", "TELECONSULT"] },
                ]}
                values={filters}
                onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))}
            />

            {error ? <div className="workspace-card__state workspace-card__state--error">{error}</div> : null}
            {loading ? <div className="workspace-card__state">Loading encounters...</div> : (
                <ModuleTable columns={["Patient", "Type", "Reason", "Status", "Attending", "Location", "Started", "Action"]} rows={rows} emptyState="No encounters found." onAction={onRowAction} />
            )}
            <ModuleApiPlan apis={["GET /encounters", "POST /encounters", "GET /encounters/{id}", "PATCH /encounters/{id}", "PATCH /encounters/{id}/status"]} />

            <EncounterCreateModal
                open={createOpen}
                form={form}
                patients={buildPatientOptions(patients)}
                doctors={doctors}
                appointments={appointments}
                error={submitError}
                submitting={submitting}
                onChange={(key, value) => setForm((current) => ({ ...current, [key]: value }))}
                onClose={() => { setCreateOpen(false); setSubmitError(""); }}
                onSubmit={onCreateSubmit}
            />
        </div>
    );
}

function InvoiceCreateModal({ open, form, patients, error, submitting, onChange, onClose, onSubmit }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card" role="dialog" aria-modal="true" aria-label="Create invoice" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Billing</span>
                        <h2>Create invoice</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>
                <form className="patient-form" onSubmit={onSubmit}>
                    <label className="patient-form__full"><span>Patient</span><select required value={form.patientId} onChange={(event) => onChange("patientId", event.target.value)}>
                        <option value="">Select patient</option>
                        {patients.map((patient) => <option value={patient.id} key={patient.id}>{patient.label}</option>)}
                    </select></label>
                    <label><span>Invoice type</span><select value={form.invoiceType} onChange={(event) => onChange("invoiceType", event.target.value)}>
                        {["CONSULTATION", "PROCEDURE", "LAB", "MEDICINE", "OTHER"].map((type) => <option value={type} key={type}>{type}</option>)}
                    </select></label>
                    <label><span>Item type</span><select value={form.itemType} onChange={(event) => onChange("itemType", event.target.value)}>
                        {["CONSULTATION_FEE", "PROCEDURE", "LAB_TEST", "MEDICINE", "OTHER"].map((type) => <option value={type} key={type}>{type}</option>)}
                    </select></label>
                    <label><span>Item name</span><input required value={form.itemName} onChange={(event) => onChange("itemName", event.target.value)} /></label>
                    <label><span>Quantity</span><input required type="number" min="1" value={form.quantity} onChange={(event) => onChange("quantity", event.target.value)} /></label>
                    <label><span>Unit amount</span><input required type="number" min="0" value={form.unitAmount} onChange={(event) => onChange("unitAmount", event.target.value)} /></label>
                    <label><span>Discount</span><input type="number" min="0" value={form.discountAmount} onChange={(event) => onChange("discountAmount", event.target.value)} /></label>
                    <label><span>Tax</span><input type="number" min="0" value={form.taxAmount} onChange={(event) => onChange("taxAmount", event.target.value)} /></label>
                    {error ? <div className="login-form__error">{error}</div> : null}
                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Creating..." : "Create invoice"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function BillingModule({ role, session }) {
    const [filters, setFilters] = useState({ status: "All" });
    const [invoices, setInvoices] = useState([]);
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshIndex, setRefreshIndex] = useState(0);
    const [createOpen, setCreateOpen] = useState(false);
    const [form, setForm] = useState(EMPTY_INVOICE_FORM);
    const [submitError, setSubmitError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        let ignore = false;
        async function loadData() {
            setLoading(true);
            setError("");
            try {
                const [invoicePage, patientPage] = await Promise.all([
                    fetchInvoices({
                        hospitalId: session.hospitalId,
                        status: filters.status === "All" ? undefined : filters.status,
                        page: 0,
                        size: 30,
                    }),
                    fetchPatients({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                ]);
                if (!ignore) {
                    setInvoices(getPageContent(invoicePage));
                    setPatients(getPageContent(patientPage));
                }
            } catch (loadError) {
                if (!ignore) setError(loadError.message || "Unable to load invoices.");
            } finally {
                if (!ignore) setLoading(false);
            }
        }

        loadData();
        return () => { ignore = true; };
    }, [filters.status, refreshIndex, session.hospitalId]);

    const patientNameMap = makeNameMap(patients);
    const rows = invoices.map((invoice) => ({
        invoice: invoice.invoiceNumber || invoice.id,
        patient: patientNameMap.get(invoice.patientId) || invoice.patientId,
        type: invoice.invoiceType || "Invoice",
        status: { pill: invoice.status, tone: getStatusTone(invoice.status) },
        total: `Rs ${invoice.totalAmount ?? 0}`,
        due: `Rs ${invoice.dueAmount ?? 0}`,
        action: Number(invoice.dueAmount || 0) > 0 ? "Pay" : "Open",
        __invoice: invoice,
    }));

    const onCreateSubmit = async (event) => {
        event.preventDefault();
        setSubmitError("");
        setSubmitting(true);
        try {
            await createInvoice({
                hospitalId: session.hospitalId,
                patientId: form.patientId,
                invoiceType: form.invoiceType,
                discountAmount: Number(form.discountAmount || 0),
                taxAmount: Number(form.taxAmount || 0),
                items: [{
                    itemName: form.itemName,
                    itemType: form.itemType,
                    quantity: Number(form.quantity),
                    unitAmount: Number(form.unitAmount),
                }],
            });
            setForm(EMPTY_INVOICE_FORM);
            setCreateOpen(false);
            setRefreshIndex((current) => current + 1);
        } catch (submitErrorValue) {
            setSubmitError(submitErrorValue.message || "Unable to create invoice.");
        } finally {
            setSubmitting(false);
        }
    };

    const onRowAction = async (row) => {
        if (row.action !== "Pay") return;
        setError("");
        try {
            await createPayment({
                hospitalId: session.hospitalId,
                patientId: row.__invoice.patientId,
                invoiceId: row.__invoice.id,
                amount: Number(row.__invoice.dueAmount || 0),
                paymentMethod: "UPI",
                status: "SUCCESS",
                transactionReference: `UI-${Date.now()}`,
            });
            setRefreshIndex((current) => current + 1);
        } catch (paymentError) {
            setError(paymentError.message || "Unable to collect payment.");
        }
    };

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">Billing</span>
                    <h1>Billing</h1>
                    <p>Create invoices and collect simple payments from the counter workflow.</p>
                </div>
                <div className="module-heading__actions">
                    <button type="button" className="secondary-button" onClick={() => setRefreshIndex((current) => current + 1)}>Refresh</button>
                    {BILLING_WRITE_ROLES.has(role) ? <button type="button" className="primary-button" onClick={() => { setSubmitError(""); setCreateOpen(true); }}>Create invoice</button> : null}
                </div>
            </section>
            <FilterBar filters={[{ key: "status", label: "Status", options: ["All", "DRAFT", "ISSUED", "PARTIALLY_PAID", "PAID", "CANCELLED", "REFUNDED"] }]} values={filters} onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))} />
            {error ? <div className="workspace-card__state workspace-card__state--error">{error}</div> : null}
            {loading ? <div className="workspace-card__state">Loading invoices...</div> : (
                <ModuleTable columns={["Invoice", "Patient", "Type", "Status", "Total", "Due", "Action"]} rows={rows} emptyState="No invoices found." onAction={onRowAction} />
            )}
            <ModuleApiPlan apis={["GET /billing/invoices", "POST /billing/invoices", "GET /billing/invoices/{id}", "POST /billing/payments", "GET /billing/payments"]} />
            <InvoiceCreateModal
                open={createOpen}
                form={form}
                patients={buildPatientOptions(patients)}
                error={submitError}
                submitting={submitting}
                onChange={(key, value) => setForm((current) => ({ ...current, [key]: value }))}
                onClose={() => { setCreateOpen(false); setSubmitError(""); }}
                onSubmit={onCreateSubmit}
            />
        </div>
    );
}

function NotificationCreateModal({ open, form, patients, error, submitting, onChange, onClose, onSubmit }) {
    if (!open) return null;

    return (
        <div className="modal-backdrop" role="presentation" onClick={onClose}>
            <div className="modal-card" role="dialog" aria-modal="true" aria-label="Create notification" onClick={(event) => event.stopPropagation()}>
                <div className="workspace-drawer__header">
                    <div>
                        <span className="workspace-section-label">Notifications</span>
                        <h2>Create notification</h2>
                    </div>
                    <button type="button" className="drawer-close" onClick={onClose}>Close</button>
                </div>
                <form className="patient-form" onSubmit={onSubmit}>
                    <label className="patient-form__full"><span>Patient</span><select value={form.patientId} onChange={(event) => onChange("patientId", event.target.value)}>
                        <option value="">No patient link</option>
                        {patients.map((patient) => <option value={patient.id} key={patient.id}>{patient.label}</option>)}
                    </select></label>
                    <label><span>Channel</span><select value={form.channel} onChange={(event) => onChange("channel", event.target.value)}>
                        <option value="EMAIL">Email</option>
                        <option value="SMS">SMS</option>
                        <option value="WHATSAPP">WhatsApp</option>
                        <option value="PUSH">Push</option>
                    </select></label>
                    <label><span>Recipient name</span><input value={form.recipientName} onChange={(event) => onChange("recipientName", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Recipient address</span><input required value={form.recipientAddress} onChange={(event) => onChange("recipientAddress", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Subject</span><input value={form.subject} onChange={(event) => onChange("subject", event.target.value)} /></label>
                    <label className="patient-form__full"><span>Message</span><textarea required rows="4" value={form.body} onChange={(event) => onChange("body", event.target.value)} /></label>
                    {error ? <div className="login-form__error">{error}</div> : null}
                    <div className="patient-form__actions">
                        <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
                        <button type="submit" className="primary-button" disabled={submitting}>{submitting ? "Sending..." : "Create notification"}</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

function NotificationsModule({ role, session }) {
    const [filters, setFilters] = useState({ status: "All" });
    const [notifications, setNotifications] = useState([]);
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [refreshIndex, setRefreshIndex] = useState(0);
    const [createOpen, setCreateOpen] = useState(false);
    const [form, setForm] = useState(EMPTY_NOTIFICATION_FORM);
    const [submitError, setSubmitError] = useState("");
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        let ignore = false;
        async function loadData() {
            setLoading(true);
            setError("");
            try {
                const [notificationPage, patientPage] = await Promise.all([
                    fetchNotifications({
                        hospitalId: session.hospitalId,
                        status: filters.status === "All" ? undefined : filters.status,
                        page: 0,
                        size: 30,
                    }),
                    fetchPatients({ hospitalId: session.hospitalId, page: 0, size: 50 }),
                ]);
                if (!ignore) {
                    setNotifications(getPageContent(notificationPage));
                    setPatients(getPageContent(patientPage));
                }
            } catch (loadError) {
                if (!ignore) setError(loadError.message || "Unable to load notifications.");
            } finally {
                if (!ignore) setLoading(false);
            }
        }

        loadData();
        return () => { ignore = true; };
    }, [filters.status, refreshIndex, session.hospitalId]);

    const rows = notifications.map((notification) => ({
        recipient: notification.recipientName || notification.recipientAddress,
        channel: notification.channel,
        subject: notification.subject || notification.sourceEventType || "Notification",
        status: { pill: notification.status, tone: getStatusTone(notification.status) },
        source: notification.sourceService || "manual",
        sent: formatDateTime(notification.sentAt || notification.createdAt),
        action: "Open",
    }));

    const onCreateSubmit = async (event) => {
        event.preventDefault();
        setSubmitError("");
        setSubmitting(true);
        try {
            await createNotification({
                hospitalId: session.hospitalId,
                patientId: form.patientId || null,
                channel: form.channel,
                recipientAddress: form.recipientAddress,
                recipientName: form.recipientName || null,
                subject: form.subject || null,
                body: form.body,
            });
            setForm(EMPTY_NOTIFICATION_FORM);
            setCreateOpen(false);
            setRefreshIndex((current) => current + 1);
        } catch (submitErrorValue) {
            setSubmitError(submitErrorValue.message || "Unable to create notification.");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="workspace-stack">
            <section className="module-heading">
                <div>
                    <span className="workspace-eyebrow">Notifications</span>
                    <h1>Notifications</h1>
                    <p>View messages created by other services and send a manual patient notification.</p>
                </div>
                <div className="module-heading__actions">
                    <button type="button" className="secondary-button" onClick={() => setRefreshIndex((current) => current + 1)}>Refresh</button>
                    {NOTIFICATION_WRITE_ROLES.has(role) ? <button type="button" className="primary-button" onClick={() => { setSubmitError(""); setCreateOpen(true); }}>Create notification</button> : null}
                </div>
            </section>
            <FilterBar filters={[{ key: "status", label: "Status", options: ["All", "PENDING", "SENT", "FAILED", "CANCELLED"] }]} values={filters} onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))} />
            {error ? <div className="workspace-card__state workspace-card__state--error">{error}</div> : null}
            {loading ? <div className="workspace-card__state">Loading notifications...</div> : (
                <ModuleTable columns={["Recipient", "Channel", "Subject", "Status", "Source", "Sent", "Action"]} rows={rows} emptyState="No notifications found." />
            )}
            <ModuleApiPlan apis={["GET /notifications", "POST /notifications", "GET /notifications/{id}"]} />
            <NotificationCreateModal
                open={createOpen}
                form={form}
                patients={buildPatientOptions(patients)}
                error={submitError}
                submitting={submitting}
                onChange={(key, value) => setForm((current) => ({ ...current, [key]: value }))}
                onClose={() => { setCreateOpen(false); setSubmitError(""); }}
                onSubmit={onCreateSubmit}
            />
        </div>
    );
}

function GenericModule({ moduleKey, role }) {
    const content = MODULE_CONTENT[moduleKey];
    const filtersMeta = getModuleFilters(moduleKey, role);
    const initialFilters = useMemo(
        () => Object.fromEntries(filtersMeta.map((filter) => [filter.key, filter.type === "search" ? "" : filter.options[0]])),
        [filtersMeta],
    );
    const [filters, setFilters] = useState(initialFilters);
    const [notice, setNotice] = useState("");
    const rows = useMemo(() => {
        const raw = getModuleRows(moduleKey, role);
        return raw.filter((row) => rowMatchesFilters(row, filters));
    }, [moduleKey, role, filters]);

    useEffect(() => {
        setFilters(initialFilters);
    }, [moduleKey, role]);

    const columnsByModule = {
        appointments: ["Patient", "Type", "Reason", "Status", "Clinician", "Time", "Action"],
        encounters: ["Patient", "Type", "Reason", "Status", "Attending", "Location", "Started", "Action"],
        reports: ["Report", "Category", "LinkedTo", "Updated", "Status", "Action"],
    };

    if (moduleKey === "billing") {
        return (
            <div className="workspace-stack">
                <section className="module-heading">
                    <div>
                        <span className="workspace-eyebrow">{content.title}</span>
                        <h1>{content.title}</h1>
                        <p>{getModuleDescription(moduleKey, role)}</p>
                    </div>
                    <button type="button" className="primary-button" disabled>{getModuleAction(moduleKey, role)}</button>
                </section>
                <FilterBar filters={filtersMeta} values={filters} onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))} />
                <section className="billing-placeholder">
                    <article className="billing-placeholder__panel">
                        <h2>Billing is intentionally parked for a later phase</h2>
                        <p>We are keeping the entry point visible in the shell so the final navigation is realistic, but pricing, invoice, and payment workflows are not implemented yet.</p>
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
                <button
                    type="button"
                    className="primary-button"
                    onClick={() => setNotice(`${getModuleAction(moduleKey, role)} will be connected once the ${MODULE_DEFS[moduleKey].label.toLowerCase()} write APIs are available.`)}
                >
                    {getModuleAction(moduleKey, role)}
                </button>
            </section>

            <FilterBar filters={filtersMeta} values={filters} onChange={(key, value) => setFilters((current) => ({ ...current, [key]: value }))} />

            {notice ? <div className="workspace-card__state">{notice}</div> : null}

            <ModuleTable
                columns={columnsByModule[moduleKey]}
                rows={rows}
                emptyState={`No ${MODULE_DEFS[moduleKey].label.toLowerCase()} matched the current filters.`}
                onAction={(row) => setNotice(`${row.action} on ${row.patient || row.report} is waiting on the corresponding detail API.`)}
            />

            <ModuleApiPlan apis={content.apis} />
        </div>
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

    if (!session) return null;

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
                        <button type="button" key={item} className={activeView === item ? "is-active" : ""} onClick={() => setActiveView(item)}>
                            <span>{MODULE_DEFS[item].icon}</span>
                            {MODULE_DEFS[item].label}
                        </button>
                    ))}
                </nav>
            </aside>

            <section className="workspace-main">
                <header className="workspace-topbar">
                    <div>
                        <span className="workspace-topbar__eyebrow">{session.hospitalCode} • {getRoleLabel(role)}</span>
                        <h2>{activeView === "dashboard" ? `Welcome, ${session.fullName}` : MODULE_DEFS[activeView].label}</h2>
                    </div>

                    <div className="workspace-topbar__actions">
                        <div className="workspace-notifications">
                            <button type="button" className={`notification-button ${notificationsOpen ? "is-open" : ""}`} onClick={() => setNotificationsOpen((current) => !current)} aria-label="Open notifications">
                                <span className="notification-button__icon" aria-hidden="true">
                                    <svg viewBox="0 0 24 24" fill="none">
                                        <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                                        <path d="M10 21h4" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                                    </svg>
                                </span>
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

                {activeView === "dashboard" ? <DashboardView role={role} onOpenModule={setActiveView} /> : null}
                {activeView === "patients" ? <PatientsModule role={role} session={session} /> : null}
                {activeView === "appointments" ? <AppointmentsModule role={role} session={session} /> : null}
                {activeView === "encounters" ? <EncountersModule role={role} session={session} /> : null}
                {activeView === "billing" ? <BillingModule role={role} session={session} /> : null}
                {activeView === "reports" ? <NotificationsModule role={role} session={session} /> : null}
            </section>
        </main>
    );
}
