INSERT INTO hospitals (
    id, name, code, address, phone, email, logo_url, primary_color, secondary_color,
    login_welcome_text, is_active, created_at, updated_at
)
SELECT
    '11111111-1111-1111-1111-111111111111',
    'CityCare Hospital',
    'citycare',
    '12 Wellness Avenue, Bengaluru',
    '08012345678',
    'admin@citycare.example.com',
    'https://storage.googleapis.com/meditrack-assets/hospital-logos/citycare/logo.png',
    '#0F766E',
    '#E0F2F1',
    'Welcome back to CityCare Hospital',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM hospitals WHERE id = '11111111-1111-1111-1111-111111111111'
);

INSERT INTO patients (
    id, patient_code, hospital_id, user_id, name, address, phone, email, aadhar, pan, date_of_birth, gender,
    is_active, created_at, updated_at
)
SELECT
    '123e4567-e89b-12d3-a456-426614174000',
    'PAT-1001',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    'John Doe',
    '123 Main St, Springfield',
    '9876543210',
    'john.doe@example.com',
    '111122223333',
    'ABCDE1234F',
    DATE '1985-06-15',
    'MALE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM patients WHERE id = '123e4567-e89b-12d3-a456-426614174000'
);

INSERT INTO patients (
    id, patient_code, hospital_id, user_id, name, address, phone, email, aadhar, pan, date_of_birth, gender,
    is_active, created_at, updated_at
)
SELECT
    '123e4567-e89b-12d3-a456-426614174001',
    'PAT-1002',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    'Jane Smith',
    '456 Elm St, Shelbyville',
    '9876501234',
    'jane.smith@example.com',
    '222233334444',
    'PQRSX6789L',
    DATE '1990-09-23',
    'FEMALE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM patients WHERE id = '123e4567-e89b-12d3-a456-426614174001'
);

INSERT INTO patient_diseases (
    id, patient_id, disease_name, disease_code, is_chronic, diagnosed_at, notes, created_at, updated_at
)
SELECT
    '33333333-3333-3333-3333-333333333333',
    '123e4567-e89b-12d3-a456-426614174000',
    'Type 2 Diabetes',
    'E11',
    TRUE,
    DATE '2023-02-10',
    'On regular medication and diet plan.',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM patient_diseases WHERE id = '33333333-3333-3333-3333-333333333333'
);

INSERT INTO medical_professionals (
    id, hospital_id, user_id, name, role_type, specialty, registration_number, phone, email,
    consultation_fee, is_active, created_at, updated_at
)
SELECT
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    NULL,
    'Dr. Asha Mehra',
    'DOCTOR',
    'General Medicine',
    'KMC-12345',
    '9876511111',
    'asha.mehra@citycare.example.com',
    700.00,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM medical_professionals WHERE id = '44444444-4444-4444-4444-444444444444'
);
