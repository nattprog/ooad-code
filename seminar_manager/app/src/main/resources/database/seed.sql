INSERT
OR IGNORE INTO users (username, password, full_name, email, role)
VALUES
  ('student1', 'pass123', 'Alice Tan', 'alice@student.com', 'STUDENT'),
  ('student2', 'pass123', 'Bob Lim', 'bob@student.com', 'STUDENT'),
  ('student3', 'pass123', 'Charlie Wong', 'charlie@student.com', 'STUDENT'),
  ('evaluator1', 'pass123', 'Dr. Eva Lee', 'eva@uni.com', 'EVALUATOR'),
  ('evaluator2', 'pass123', 'Dr. Mark Ong', 'mark@uni.com', 'EVALUATOR'),
  ('coordinator1', 'pass123', 'Prof. John Tan', 'john@uni.com', 'COORDINATOR');

INSERT
OR IGNORE INTO students (student_id, user_id)
VALUES
  ('S001', 1),
  ('S002', 2),
  ('S003', 3);

INSERT
OR IGNORE INTO evaluators (evaluator_id, user_id)
VALUES
  ('E001', 4),
  ('E002', 5);

INSERT
OR IGNORE INTO coordinators (coordinator_id, user_id)
VALUES
  ('C001', 6);

INSERT
OR IGNORE INTO seminars (title, description, location, start_time, end_time)
VALUES
  (
    'Final Year Project Seminar 2026',
    'Presentation of final year research projects',
    'Main Hall A',
    '2026-03-10 09:00:00',
    '2026-03-10 17:00:00'
  );

-- INSERT
-- OR IGNORE INTO sessions (seminar_id, presentation_type, time_slots_count, time_slots_duration, start_time, end_time)
-- VALUES
--   (1, 'ORAL', 3, 30, '2026-03-10 09:00:00', '2026-03-10 10:30:00'),
--   (1, 'POSTER', 2, 30, '2026-03-10 11:00:00', '2026-03-10 12:00:00');
INSERT
OR IGNORE INTO submissions (
  seminar_id,
  session_id,
  student_user_id,
  research_title,
  research_abstract,
  supervisor_name,
  presentation_type,
  status
)
VALUES
  (
    1,
    1,
    1,
    'AI for Smart Traffic',
    'Using AI to optimize traffic flow',
    'Dr. Smith',
    'ORAL',
    'APPROVED'
  ),
  (
    1,
    1,
    2,
    'Blockchain Voting System',
    'Secure voting using blockchain',
    'Dr. Lee',
    'ORAL',
    'APPROVED'
  ),
  (1, 2, 3, 'AR Learning Tools', 'Augmented reality for education', 'Dr. Chen', 'POSTER', 'APPROVED');

-- INSERT
-- OR IGNORE INTO time_slots (session_id, submission_id, start_time, end_time)
-- VALUES
--   -- ORAL session
--   (1, 1, '2026-03-10 09:00:00', '2026-03-10 09:30:00'),
--   (1, 2, '2026-03-10 09:30:00', '2026-03-10 10:00:00'),
--   (1, NULL, '2026-03-10 10:00:00', '2026-03-10 10:30:00'),
--   -- POSTER session
--   (2, 3, '2026-03-10 11:00:00', '2026-03-10 11:30:00'),
--   (2, NULL, '2026-03-10 11:30:00', '2026-03-10 12:00:00');
INSERT
OR IGNORE INTO evaluator_assignments (session_id, evaluator_user_id)
VALUES
  (1, 4),
  (1, 5),
  (2, 4);

INSERT
OR IGNORE INTO evaluations (
  evaluator_assignment_id,
  submission_id,
  problem_clarity_score,
  methodology_score,
  results_score,
  presentation_score,
  total_score,
  comments
)
VALUES
  (1, 1, 8, 9, 8, 9, 34, 'Very strong presentation'),
  (2, 1, 7, 8, 8, 8, 31, 'Good work'),
  (1, 2, 9, 9, 9, 9, 36, 'Excellent research'),
  (3, 3, 8, 7, 8, 7, 30, 'Creative idea');

INSERT
OR IGNORE INTO awards (seminar_id, submission_id, award_type)
VALUES
  (1, 2, 'BEST_ORAL'),
  (1, 3, 'BEST_POSTER');

INSERT
OR IGNORE INTO reports (seminar_id, report_type, file_path, report_content)
VALUES
  (1, 'SESSION_SCHEDULE', '/reports/session_schedule.pdf', 'Session schedule details'),
  (1, 'EVALUATION_SUMMARY', '/reports/evaluation_summary.pdf', 'Evaluation statistics'),
  (1, 'AWARD_LIST', '/reports/awards.pdf', 'List of award winners');