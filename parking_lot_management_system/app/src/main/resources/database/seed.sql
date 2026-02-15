-- ============================================
-- Parking Lot Management System Seed Data
-- ============================================
-- This file contains sample data for testing and demonstration
-- Including various scenarios: normal parking, overstaying, reserved spot violations

-- ============================================
-- SEED VEHICLES
-- ============================================

-- Normal vehicles (for regular parking demo)
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('ABC1234', 'Car');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('BIKE001', 'Motorcycle');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('TRUCK01', 'Truck');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('HANDI01', 'Handicapped');

-- Vehicles for overstay scenario (OVER-prefix)
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('OVER001', 'Car');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('OVER002', 'Car');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('OVER003', 'Motorcycle');

-- Vehicles for reserved spot violation (RSRV-prefix)
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('RSRV001', 'Car');
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('RSRV002', 'Truck');

-- Vehicles for combined violations (COMBO-prefix)
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('COMBO01', 'Car');

-- Vehicles with unpaid fines from previous sessions (FINE-prefix)
INSERT OR IGNORE INTO vehicles (vehicle_id, vehicle_type) VALUES ('FINE001', 'Car');

-- ============================================
-- SEED ACTIVE TICKETS (Currently Parked)
-- ============================================

-- Scenario 1: Normal parking (< 24 hours, ABC1234)
-- Parked 2 hours ago
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-ABC1234-F1-R1-S2-' || datetime('now', '-2 hours'),
    'ABC1234',
    'F1-R1-S2',
    datetime('now', '-2 hours'),
    NULL,
    NULL,
    NULL
);

-- Update spot occupancy for ABC1234
UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'ABC1234' WHERE spot_id = 'F1-R1-S2';

-- Scenario 2: Normal motorcycle parking (BIKE001)
-- Parked 1 hour ago
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-BIKE001-F1-R1-S1-' || datetime('now', '-1 hour'),
    'BIKE001',
    'F1-R1-S1',
    datetime('now', '-1 hour'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'BIKE001' WHERE spot_id = 'F1-R1-S1';

-- Scenario 3: Overstaying - 30 hours (OVER001)
-- Should trigger overstay fine (6 hours over the 24-hour limit)
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-OVER001-F2-R1-S2-' || datetime('now', '-30 hours'),
    'OVER001',
    'F2-R1-S2',
    datetime('now', '-30 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'OVER001' WHERE spot_id = 'F2-R1-S2';

-- Scenario 4: Major overstaying - 50 hours (OVER002)
-- Should trigger significant overstay fine (26 hours over)
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-OVER002-F2-R2-S2-' || datetime('now', '-50 hours'),
    'OVER002',
    'F2-R2-S2',
    datetime('now', '-50 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'OVER002' WHERE spot_id = 'F2-R2-S2';

-- Scenario 5: Extreme overstaying - 80 hours (OVER003)
-- Should trigger maximum overstay fine (56 hours over)
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-OVER003-F2-R1-S1-' || datetime('now', '-80 hours'),
    'OVER003',
    'F2-R1-S1',
    datetime('now', '-80 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'OVER003' WHERE spot_id = 'F2-R1-S1';

-- Scenario 6: Unauthorized reserved spot usage (RSRV001)
-- Parked in RESERVED spot for 5 hours
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-RSRV001-F1-R1-S4-' || datetime('now', '-5 hours'),
    'RSRV001',
    'F1-R1-S4',
    datetime('now', '-5 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'RSRV001' WHERE spot_id = 'F1-R1-S4';

-- Scenario 7: Reserved spot violation with overstaying (COMBO01)
-- Parked in RESERVED spot for 30 hours (both violations)
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-COMBO01-F2-R1-S4-' || datetime('now', '-30 hours'),
    'COMBO01',
    'F2-R1-S4',
    datetime('now', '-30 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'COMBO01' WHERE spot_id = 'F2-R1-S4';

-- Scenario 8: Handicapped vehicle in handicapped spot (HANDI01)
-- Should get free parking (RM 0/hour)
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-HANDI01-F1-R1-S3-' || datetime('now', '-3 hours'),
    'HANDI01',
    'F1-R1-S3',
    datetime('now', '-3 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'HANDI01' WHERE spot_id = 'F1-R1-S3';

-- ============================================
-- SEED UNPAID FINES (from previous sessions)
-- ============================================

-- Vehicle FINE001 has unpaid fines from previous parking sessions
-- These should appear when FINE001 exits in the future

-- Previous overstay fine (30 days ago)
INSERT OR IGNORE INTO fines (
    vehicle_id,
    ticket_id,
    fine_scheme,
    fine_type,
    violating_hours,
    amount,
    is_paid,
    created_datetime
) VALUES (
    'FINE001',
    'T-FINE001-OLD-001',
    'FIXED',
    'OVERSTAY',
    10,
    50.00,
    0,
    datetime('now', '-30 days')
);

-- Previous reserved spot violation (15 days ago)
INSERT OR IGNORE INTO fines (
    vehicle_id,
    ticket_id,
    fine_scheme,
    fine_type,
    violating_hours,
    amount,
    is_paid,
    created_datetime
) VALUES (
    'FINE001',
    'T-FINE001-OLD-002',
    'FIXED',
    'UNAUTHORIZED_RESERVED',
    5,
    50.00,
    0,
    datetime('now', '-15 days')
);

-- Now park FINE001 again (currently parked, 2 hours)
-- When it exits, it should show the 2 unpaid fines + current parking fee
INSERT OR IGNORE INTO tickets (
    ticket_id, 
    vehicle_id, 
    spot_id, 
    entry_datetime, 
    exit_datetime, 
    duration_hours, 
    payment_id
) VALUES (
    'T-FINE001-F3-R1-S2-' || datetime('now', '-2 hours'),
    'FINE001',
    'F3-R1-S2',
    datetime('now', '-2 hours'),
    NULL,
    NULL,
    NULL
);

UPDATE parking_spots SET is_occupied = 1, current_vehicle_id = 'FINE001' WHERE spot_id = 'F3-R1-S2';

-- ============================================
-- SEED COMPLETED TICKETS (Historical Data)
-- ============================================

-- Sample completed parking session 1
INSERT OR IGNORE INTO tickets (
    ticket_id,
    vehicle_id,
    spot_id,
    entry_datetime,
    exit_datetime,
    duration_hours,
    payment_id
) VALUES (
    'T-ABC1234-HIST-001',
    'ABC1234',
    'F1-R2-S1',
    datetime('now', '-7 days', '+10 hours'),
    datetime('now', '-7 days', '+13 hours'),
    3,
    'PAY-HIST-001'
);

-- Payment for completed session 1
INSERT OR IGNORE INTO payments (
    payment_id,
    ticket_id,
    parking_fee,
    fine_amount,
    total_amount,
    payment_method,
    payment_datetime
) VALUES (
    'PAY-HIST-001',
    'T-ABC1234-HIST-001',
    15.00,
    0.00,
    15.00,
    'Card',
    datetime('now', '-7 days', '+13 hours')
);

-- Revenue from completed session 1
INSERT OR IGNORE INTO revenue (amount, revenue_type, recorded_datetime)
VALUES (15.00, 'PARKING', datetime('now', '-7 days', '+13 hours'));

-- Sample completed session 2 with fine
INSERT OR IGNORE INTO tickets (
    ticket_id,
    vehicle_id,
    spot_id,
    entry_datetime,
    exit_datetime,
    duration_hours,
    payment_id
) VALUES (
    'T-OVER001-HIST-001',
    'OVER001',
    'F2-R1-S1',
    datetime('now', '-5 days', '+8 hours'),
    datetime('now', '-5 days', '+35 hours'),
    27,
    'PAY-HIST-002'
);

-- Fine for overstaying (3 hours over 24-hour limit)
INSERT OR IGNORE INTO fines (
    vehicle_id,
    ticket_id,
    fine_scheme,
    fine_type,
    violating_hours,
    amount,
    is_paid,
    created_datetime
) VALUES (
    'OVER001',
    'T-OVER001-HIST-001',
    'FIXED',
    'OVERSTAY',
    3,
    50.00,
    1,
    datetime('now', '-5 days', '+35 hours')
);

-- Payment for session with fine
INSERT OR IGNORE INTO payments (
    payment_id,
    ticket_id,
    parking_fee,
    fine_amount,
    total_amount,
    payment_method,
    payment_datetime
) VALUES (
    'PAY-HIST-002',
    'T-OVER001-HIST-001',
    135.00,
    50.00,
    185.00,
    'Cash',
    datetime('now', '-5 days', '+35 hours')
);

-- Revenue from completed session 2
INSERT OR IGNORE INTO revenue (amount, revenue_type, recorded_datetime)
VALUES (135.00, 'PARKING', datetime('now', '-5 days', '+35 hours'));

INSERT OR IGNORE INTO revenue (amount, revenue_type, recorded_datetime)
VALUES (50.00, 'FINE', datetime('now', '-5 days', '+35 hours'));

-- ============================================
-- SUMMARY OF SEED DATA
-- ============================================
-- Active Tickets (Currently Parked):
--   ABC1234  - Normal parking (2 hours) - Regular spot
--   BIKE001  - Normal parking (1 hour) - Compact spot
--   OVER001  - Overstaying (30 hours) - Will have 6-hour overstay fine
--   OVER002  - Major overstay (50 hours) - Will have 26-hour overstay fine
--   OVER003  - Extreme overstay (80 hours) - Will have 56-hour overstay fine
--   RSRV001  - Reserved spot violation (5 hours)
--   COMBO01  - Both violations (30 hours in reserved spot)
--   HANDI01  - Handicapped in handicapped spot (3 hours) - FREE
--   FINE001  - Currently parked (2 hours) - Has 2 unpaid fines from before
--
-- Total Active Vehicles: 9
-- Total Historical Sessions: 2
-- Total Unpaid Fines: 2 (for FINE001)
-- Total Revenue Recorded: RM 200.00
-- ============================================
