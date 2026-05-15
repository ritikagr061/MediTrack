INSERT INTO hospital_proxy (
    hospital_id, hospital_code, hospital_name, logo_url, hospital_message, primary_color,
    secondary_color, is_active, last_synced_at, created_at, updated_at
) VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'citycare',
        'CityCare Hospital',
        'https://storage.googleapis.com/meditrack-assets/hospital-logos/citycare/logo.png',
        'Welcome back to CityCare Hospital',
        '#0F766E',
        '#E0F2F1',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'northstar',
        'NorthStar Multispeciality Hospital',
        'https://storage.googleapis.com/meditrack-assets/hospital-logos/northstar/logo.png',
        'Welcome to NorthStar Multispeciality Hospital',
        '#1D4ED8',
        '#DBEAFE',
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (hospital_id) DO NOTHING;
