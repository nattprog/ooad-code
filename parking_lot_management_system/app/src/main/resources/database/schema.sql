-- ============================================
-- Parking Lot Management System Database Schema
-- ============================================

-- Drop tables if they exist (for clean setup)
-- Note: Comment out the DROP statements for production
DROP TABLE IF EXISTS revenue;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS parking_spots;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS parking_lot_config;

-- ============================================
-- PARKING LOT CONFIGURATION TABLE
-- Stores global parking lot settings
-- ============================================
CREATE TABLE IF NOT EXISTS parking_lot_config (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Only one row allowed
    fine_scheme TEXT NOT NULL DEFAULT 'FIXED',
    total_floors INTEGER NOT NULL DEFAULT 5,
    created_datetime TEXT NOT NULL,
    last_updated_datetime TEXT NOT NULL
);

-- Insert default parking lot configuration
INSERT OR IGNORE INTO parking_lot_config (id, fine_scheme, total_floors, created_datetime, last_updated_datetime)
VALUES (1, 'FIXED', 5, datetime('now'), datetime('now'));

-- ============================================
-- PARKING SPOTS TABLE
-- Stores information about each parking spot
-- ============================================
CREATE TABLE IF NOT EXISTS parking_spots (
    spot_id TEXT PRIMARY KEY,
    spot_number INTEGER NOT NULL,
    row_number INTEGER NOT NULL,
    floor_number INTEGER NOT NULL,
    spot_type TEXT NOT NULL CHECK (spot_type IN ('COMPACT', 'REGULAR', 'HANDICAPPED', 'RESERVED')),
    is_occupied BOOLEAN NOT NULL DEFAULT 0,
    current_vehicle_id TEXT,
    FOREIGN KEY (current_vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE SET NULL
);

-- Create index for faster spot lookups
CREATE INDEX IF NOT EXISTS idx_spots_floor_occupied ON parking_spots(floor_number, is_occupied);
CREATE INDEX IF NOT EXISTS idx_spots_type ON parking_spots(spot_type);

-- ============================================
-- VEHICLES TABLE
-- Stores information about all vehicles
-- ============================================
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id TEXT PRIMARY KEY,
    vehicle_type TEXT NOT NULL CHECK (vehicle_type IN ('Motorcycle', 'Car', 'Truck', 'Handicapped'))
);

-- Create index for vehicle type queries
CREATE INDEX IF NOT EXISTS idx_vehicles_type ON vehicles(vehicle_type);

-- ============================================
-- TICKETS TABLE
-- Stores parking ticket information
-- ============================================
CREATE TABLE IF NOT EXISTS tickets (
    ticket_id TEXT PRIMARY KEY,
    vehicle_id TEXT NOT NULL,
    spot_id TEXT NOT NULL,
    entry_datetime TEXT NOT NULL,
    exit_datetime TEXT,
    duration_hours INTEGER,
    payment_id TEXT,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE,
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id) ON DELETE CASCADE,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE SET NULL
);

-- Create indexes for faster ticket queries
CREATE INDEX IF NOT EXISTS idx_tickets_vehicle ON tickets(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_tickets_active ON tickets(exit_datetime) WHERE exit_datetime IS NULL;
CREATE INDEX IF NOT EXISTS idx_tickets_entry_datetime ON tickets(entry_datetime);

-- ============================================
-- PAYMENTS TABLE
-- Stores payment transaction records
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id TEXT PRIMARY KEY,
    ticket_id TEXT NOT NULL,
    parking_fee REAL NOT NULL CHECK (parking_fee >= 0),
    fine_amount REAL NOT NULL DEFAULT 0 CHECK (fine_amount >= 0),
    total_amount REAL NOT NULL CHECK (total_amount >= 0),
    payment_method TEXT NOT NULL CHECK (payment_method IN ('Cash', 'Card')),
    payment_datetime TEXT NOT NULL,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE
);

-- Create index for payment queries
CREATE INDEX IF NOT EXISTS idx_payments_datetime ON payments(payment_datetime);

-- ============================================
-- FINES TABLE
-- Stores fine records
-- ============================================
CREATE TABLE IF NOT EXISTS fines (
    fine_id INTEGER PRIMARY KEY AUTOINCREMENT,
    vehicle_id TEXT NOT NULL,
    ticket_id TEXT,
    fine_scheme TEXT NOT NULL CHECK (fine_scheme IN ('FIXED', 'PROGRESSIVE', 'HOURLY')),
    fine_type TEXT NOT NULL CHECK (fine_type IN ('OVERSTAY', 'UNAUTHORIZED_RESERVED')),
    violating_hours INTEGER NOT NULL CHECK (violating_hours > 0),
    amount REAL NOT NULL CHECK (amount >= 0),
    is_paid BOOLEAN NOT NULL DEFAULT 0,
    created_datetime TEXT NOT NULL,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE SET NULL
);

-- Create indexes for fine queries
CREATE INDEX IF NOT EXISTS idx_fines_vehicle_unpaid ON fines(vehicle_id, is_paid);
CREATE INDEX IF NOT EXISTS idx_fines_created ON fines(created_datetime);

-- ============================================
-- REVENUE TABLE
-- Tracks all revenue collected
-- ============================================
CREATE TABLE IF NOT EXISTS revenue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL CHECK (amount >= 0),
    revenue_type TEXT NOT NULL CHECK (revenue_type IN ('PARKING', 'FINE')),
    recorded_datetime TEXT NOT NULL
);

-- Create index for revenue reporting
CREATE INDEX IF NOT EXISTS idx_revenue_type_datetime ON revenue(revenue_type, recorded_datetime);

-- ============================================
-- VIEWS FOR REPORTING
-- ============================================

-- View for active parking sessions
CREATE VIEW IF NOT EXISTS v_active_parking AS
SELECT 
    t.ticket_id,
    t.vehicle_id,
    v.vehicle_type,
    t.spot_id,
    ps.spot_type,
    ps.floor_number,
    t.entry_datetime,
    CAST((julianday('now') - julianday(t.entry_datetime)) * 24 AS INTEGER) as hours_parked
FROM tickets t
JOIN vehicles v ON t.vehicle_id = v.vehicle_id
JOIN parking_spots ps ON t.spot_id = ps.spot_id
WHERE t.exit_datetime IS NULL;

-- View for revenue summary
CREATE VIEW IF NOT EXISTS v_revenue_summary AS
SELECT 
    revenue_type,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(amount) as min_amount,
    MAX(amount) as max_amount
FROM revenue
GROUP BY revenue_type;

-- View for spot occupancy by type
CREATE VIEW IF NOT EXISTS v_occupancy_by_type AS
SELECT 
    spot_type,
    COUNT(*) as total_spots,
    SUM(CASE WHEN is_occupied = 1 THEN 1 ELSE 0 END) as occupied_spots,
    CAST(SUM(CASE WHEN is_occupied = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) AS REAL) as occupancy_percentage
FROM parking_spots
GROUP BY spot_type;

-- ============================================
-- TRIGGERS FOR DATA INTEGRITY
-- ============================================

-- Trigger to update last_updated_datetime when fine_scheme changes
CREATE TRIGGER IF NOT EXISTS trg_update_parking_lot_config
AFTER UPDATE ON parking_lot_config
BEGIN
    UPDATE parking_lot_config 
    SET last_updated_datetime = datetime('now')
    WHERE id = 1;
END;

-- Trigger to validate parking spot assignment
CREATE TRIGGER IF NOT EXISTS trg_validate_spot_assignment
BEFORE UPDATE OF is_occupied ON parking_spots
WHEN NEW.is_occupied = 1 AND OLD.is_occupied = 1
BEGIN
    SELECT RAISE(ABORT, 'Spot is already occupied');
END;