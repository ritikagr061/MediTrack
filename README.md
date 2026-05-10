# MediTrack

MediTrack is a hospital-focused platform with branded hospital login, role-based workspaces, patient management, appointments, encounters, and a staged billing area.

## Prerequisites

- Docker Desktop
- Node.js 22.x
- npm

## Start The Backend

From the repo root:

```powershell
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

If you do not need a full clean reset every time, use:

```powershell
docker compose build auth-service patient-service api-gateway
docker compose up -d
```

## Start The Frontend

Open a new terminal and run:

```powershell
cd meditrack-frontend
npm install
npm run dev -- --host 0.0.0.0 --port 3000
```

## Local URLs

- Frontend: `http://localhost:3000`
- Default branded login: `http://localhost:3000/login/citycare`
- API Gateway: `http://localhost:8000`
- Auth service actuator port mapping: `http://localhost:6002`
- Patient service actuator port mapping: `http://localhost:6003`

## Demo Login

For any hospital page that has been synced into auth, the login screen prefills:

- Username: `guestAdmin`
- Password: `12345678`

For the seeded demo hospital, use:

- URL: `http://localhost:3000/login/citycare`

## Current Frontend Scope

- Branded hospital login page
- Patient-only public registration page
- Role-based authenticated workspace
- Role-aware navigation for:
  - Dashboard
  - Patients
  - Appointments
  - Encounters
  - Billing
  - Reports
- Notification panel in the top bar
- Mocked list views with filters for patients, appointments, encounters, and reports

## Notes

- `billing` is intentionally present in the shell but is still a future workflow area.
- The homepage and module pages are currently frontend-driven with mocked data so the product flow can be demonstrated before all summary and list APIs are implemented.
- If login or demo users behave unexpectedly after schema changes, prefer the clean Docker reset command with `docker compose down -v`.
