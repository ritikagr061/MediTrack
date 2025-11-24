# 🏥 Patient Service Overview

**Patient Service** mainly consists of three modules:
1. **Patients**
2. **Appointments / Scheduling**
3. **Encounters**

---

## 🩺 Encounters

An **Encounter** is a visit or interaction between a patient and the hospital (e.g., clinic visit, ER visit, or admission).

This is how the **encounter listing** looks for a hospital admin:  
![Encounters Listing](images/encounters_listings.png)

---

## 🚀 Starting an Encounter

Different types of encounters include:

- **OPD / Clinic Visit** – Patient comes for a routine consultation (not admitted).
- **IPD / Admission** – Patient is admitted to a ward, room, or bed and stays for treatment.
- **ER (Emergency Room)** – Urgent, unplanned visit.
- **Virtual / Teleconsult** – Online video or phone consultation.

---

## 🧾 Encounter Metadata (Basic Details)

- **Type:** OPD, IPD, ER, or Teleconsult (explained above).
- **Reason / Chief Complaint:** The main problem in the patient’s words (e.g., “fever for 3 days”).
- **Location:** Where the patient is managed (e.g., OPD Room 5, Ward 3, Bed 12).
- **Attending Doctor:** The doctor responsible for the encounter.
- **Status Lifecycle:**
An encounter can be in one of the following states:
**PLANNED**
**IN_PROGRESS**
**ON_HOLD**
**FINISHED**
**CANCELLED**

---

## 🔗 Linkages (Connections to Other Items)

1. **Patient** – The person receiving care.
2. **Appointment** – Scheduled time slot that led to this encounter (if any).

> *Only two linkages for now.*

---

## 🧭 New Streamlined Encounter Page Tabs

The new **Encounter Page** will include the following tabs:

1. **Problems & Diagnosis** – Patient complaints and doctor’s working/final diagnosis.
2. **Tests & Orders** – Lab and imaging orders along with results (PDFs, reports, etc.).
3. **Medications** – All prescribed drugs with dose, route, and duration.
4. **Procedures Performed** – Record of any procedures or minor operations.
5. **Attachments** – Upload previous reports, discharge summaries, or images.
6. **Collaboration** – Doctors can collaborate with others by tagging individuals using `@keyword`.

Each tab directly maps to one part of the doctor’s workflow — **clear, concise, and complete** for both outpatient and inpatient encounters.

---

## 🖼️ Encounter Page Example

This is how the **Encounters Page** looks:  
![Encounter Page](images/encounter_module.png)
