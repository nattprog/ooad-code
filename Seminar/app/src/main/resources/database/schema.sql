-- ============================================================
-- SEMINAR MANAGEMENT SYSTEM - COMPLETE DATABASE SCHEMA
-- ============================================================
-- Drop existing tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS reports;

DROP TABLE IF EXISTS awards;

DROP TABLE IF EXISTS evaluations;

DROP TABLE IF EXISTS evaluator_assignments;

DROP TABLE IF EXISTS time_slots;

DROP TABLE IF EXISTS submissions;

DROP TABLE IF EXISTS sessions;

DROP TABLE IF EXISTS seminars;

DROP TABLE IF EXISTS students;

DROP TABLE IF EXISTS evaluators;

DROP TABLE IF EXISTS coordinators;

DROP TABLE IF EXISTS users;

-- ============================================================
-- USERS TABLE
-- Base table for all user types
-- ============================================================
CREATE TABLE
  users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('STUDENT', 'EVALUATOR', 'COORDINATOR')),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  );

-- ============================================================
-- STUDENT TABLE
-- Student is a User
-- ============================================================
CREATE TABLE
  students (
    student_id TEXT PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
  );

-- ============================================================
-- EVALUATOR TABLE
-- Evaluator is a User
-- ============================================================
CREATE TABLE
  evaluators (
    evaluator_id TEXT PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
  );

-- ============================================================
-- COORDINATOR TABLE
-- Coordinator is a User
-- ============================================================
CREATE TABLE
  coordinators (
    coordinator_id TEXT PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
  );

-- ============================================================
-- SEMINAR TABLE
-- A Seminar is created by Coordinators
-- ============================================================
CREATE TABLE
  seminars (
    seminar_id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    CHECK (end_time > start_time)
  );

-- ============================================================
-- SESSION TABLE
-- A Session belongs to a Seminar
-- A Seminar has at least 1 Session
-- A Session is created by Coordinators
-- ============================================================
CREATE TABLE
  sessions (
    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
    seminar_id INTEGER NOT NULL,
    presentation_type TEXT NOT NULL CHECK (presentation_type IN ('ORAL', 'POSTER')),
    time_slots_count INTEGER NOT NULL CHECK (time_slots_count > 0),
    time_slots_duration INTEGER NOT NULL CHECK (time_slots_duration IN (15, 30, 45, 60)),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    FOREIGN KEY (seminar_id) REFERENCES seminars (seminar_id) ON DELETE CASCADE,
    CHECK (end_time > start_time)
  );

-- ============================================================
-- SUBMISSION TABLE
-- A Submission belongs to a Student
-- A Student may have 0 or more Submissions
-- Submissions are created by Students
-- A Submission is for a Seminar
-- A Seminar may have 0 or more Submissions
-- A Submission may be assigned to a Session
-- Submissions are assigned to a Session by Coordinators
-- ============================================================
CREATE TABLE
  submissions (
    submission_id INTEGER PRIMARY KEY AUTOINCREMENT,
    seminar_id INTEGER NOT NULL,
    session_id INTEGER,
    student_user_id INTEGER NOT NULL,
    research_title TEXT NOT NULL,
    research_abstract TEXT NOT NULL,
    supervisor_name TEXT NOT NULL,
    presentation_type TEXT NOT NULL CHECK (presentation_type IN ('ORAL', 'POSTER')),
    status TEXT DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seminar_id) REFERENCES seminars (seminar_id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES sessions (session_id) ON DELETE SET NULL,
    FOREIGN KEY (student_user_id) REFERENCES users (user_id) ON DELETE CASCADE
  );

-- ============================================================
-- TIME_SLOT TABLE
-- A TimeSlot belongs to a Session
-- A Session has at least 1 TimeSlot
-- A TimeSlot may have a Submission
-- A Submission may be assigned to a TimeSlot
-- TimeSlots are created by the system
-- Submissions may be assigned to a TimeSlot by Coordinators or system
-- ============================================================
CREATE TABLE
  time_slots (
    time_slot_id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id INTEGER NOT NULL,
    submission_id INTEGER,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions (session_id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions (submission_id) ON DELETE SET NULL,
    CHECK (end_time > start_time)
  );

-- ============================================================
-- EVALUATOR_ASSIGNMENT TABLE
-- An EvaluatorAssignment belongs to a Session
-- A Session has at least 1 EvaluatorAssignment
-- An EvaluatorAssignment belongs to an Evaluator
-- An Evaluator may have 0 or more EvaluatorAssignments
-- EvaluatorAssignments are created by Coordinators
-- ============================================================
CREATE TABLE
  evaluator_assignments (
    evaluator_assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id INTEGER NOT NULL,
    evaluator_user_id INTEGER NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions (session_id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    UNIQUE (session_id, evaluator_user_id)
  );

-- ============================================================
-- EVALUATION TABLE
-- An Evaluation belongs to a Submission
-- A Submission may have 0 or more Evaluations
-- An Evaluation has an EvaluatorAssignment
-- An EvaluatorAssignment has at least 1 Evaluation
-- Evaluations are created by the system
-- Evaluation fields are filled by Evaluators
-- ============================================================
CREATE TABLE
  evaluations (
    evaluation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    evaluator_assignment_id INTEGER NOT NULL,
    submission_id INTEGER NOT NULL,
    problem_clarity_score INTEGER CHECK (problem_clarity_score BETWEEN 1 AND 10),
    methodology_score INTEGER CHECK (methodology_score BETWEEN 1 AND 10),
    results_score INTEGER CHECK (results_score BETWEEN 1 AND 10),
    presentation_score INTEGER CHECK (presentation_score BETWEEN 1 AND 10),
    total_score INTEGER,
    comments TEXT,
    FOREIGN KEY (evaluator_assignment_id) REFERENCES evaluator_assignments (evaluator_assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions (submission_id) ON DELETE CASCADE
  );

-- ============================================================
-- AWARD TABLE
-- An Award belongs to a Seminar
-- A Seminar may have 0 or more Awards
-- An Award is given to a Submission
-- A Submission may have 0 or more Awards
-- ============================================================
CREATE TABLE
  awards (
    award_id INTEGER PRIMARY KEY AUTOINCREMENT,
    seminar_id INTEGER NOT NULL,
    submission_id INTEGER NOT NULL,
    award_type TEXT NOT NULL CHECK (award_type IN ('BEST_ORAL', 'BEST_POSTER', 'PEOPLES_CHOICE')),
    FOREIGN KEY (seminar_id) REFERENCES seminars (seminar_id) ON DELETE CASCADE,
    FOREIGN KEY (submission_id) REFERENCES submissions (submission_id) ON DELETE CASCADE
  );

-- ============================================================
-- REPORT TABLE
-- A Report belongs to a Seminar
-- A Seminar may have 0 or more Reports
-- ============================================================
CREATE TABLE
  reports (
    report_id INTEGER PRIMARY KEY AUTOINCREMENT,
    seminar_id INTEGER NOT NULL,
    report_type TEXT NOT NULL CHECK (report_type IN ('SESSION_SCHEDULE', 'EVALUATION_SUMMARY', 'AWARD_LIST', 'ANALYTICS')),
    generated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    file_path TEXT,
    report_content TEXT,
    FOREIGN KEY (seminar_id) REFERENCES seminars (seminar_id) ON DELETE CASCADE
  );

-- ============================================================
-- INDEXES FOR PERFORMANCE
-- ============================================================
CREATE INDEX idx_users_username ON users (username);

CREATE INDEX idx_users_role ON users (role);

CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_students_user_id ON students (user_id);

CREATE INDEX idx_evaluators_user_id ON evaluators (user_id);

CREATE INDEX idx_coordinators_user_id ON coordinators (user_id);

CREATE INDEX idx_sessions_seminar ON sessions (seminar_id);

CREATE INDEX idx_sessions_presentation_type ON sessions (presentation_type);

CREATE INDEX idx_submissions_seminar ON submissions (seminar_id);

CREATE INDEX idx_submissions_session ON submissions (session_id);

CREATE INDEX idx_submissions_student ON submissions (student_user_id);

CREATE INDEX idx_submissions_status ON submissions (status);

CREATE INDEX idx_submissions_type ON submissions (presentation_type);

CREATE INDEX idx_time_slots_session ON time_slots (session_id);

CREATE INDEX idx_time_slots_submission ON time_slots (submission_id);

CREATE INDEX idx_evaluator_assignments_session ON evaluator_assignments (session_id);

CREATE INDEX idx_evaluator_assignments_evaluator ON evaluator_assignments (evaluator_user_id);

CREATE INDEX idx_evaluations_assignment ON evaluations (evaluator_assignment_id);

CREATE INDEX idx_evaluations_submission ON evaluations (submission_id);

CREATE INDEX idx_awards_seminar ON awards (seminar_id);

CREATE INDEX idx_awards_submission ON awards (submission_id);

CREATE INDEX idx_awards_type ON awards (award_type);

CREATE INDEX idx_reports_seminar ON reports (seminar_id);

CREATE INDEX idx_reports_type ON reports (report_type);

-- ============================================================
-- END OF SCHEMA
-- ============================================================