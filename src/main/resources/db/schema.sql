-- ABOUTME: Database schema for school voting system
-- ABOUTME: Defines tables for parents, votes, and voting sessions

-- Drop tables if they exist (for development)
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS parents;
DROP TABLE IF EXISTS voting_sessions;

-- Voting sessions table
CREATE TABLE voting_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_name TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'SETUP' CHECK(status IN ('SETUP', 'VOTING', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Parents table
CREATE TABLE parents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    is_candidate BOOLEAN NOT NULL DEFAULT 0,
    has_voted BOOLEAN NOT NULL DEFAULT 0,
    session_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES voting_sessions(id) ON DELETE CASCADE
);

-- Votes table
CREATE TABLE votes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    voter_id INTEGER NOT NULL,
    candidate_id INTEGER NOT NULL,
    session_id INTEGER NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (voter_id) REFERENCES parents(id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES parents(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES voting_sessions(id) ON DELETE CASCADE,
    UNIQUE(voter_id, session_id) -- Ensure one vote per parent per session
);

-- Indexes for performance
CREATE INDEX idx_parents_session ON parents(session_id);
CREATE INDEX idx_parents_candidate ON parents(session_id, is_candidate);
CREATE INDEX idx_votes_session ON votes(session_id);
CREATE INDEX idx_votes_candidate ON votes(candidate_id, session_id);