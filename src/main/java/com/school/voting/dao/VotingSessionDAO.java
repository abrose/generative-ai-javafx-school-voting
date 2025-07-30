// ABOUTME: Data Access Object for VotingSession entities, manages session lifecycle
// ABOUTME: Handles session creation, status updates, and session queries

package com.school.voting.dao;

import com.school.voting.model.VotingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VotingSessionDAO {
    private static final Logger logger = LoggerFactory.getLogger(VotingSessionDAO.class);
    private final DatabaseManager dbManager;

    public VotingSessionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public VotingSession createSession(VotingSession session) throws SQLException {
        String sql = "INSERT INTO voting_sessions (class_name, status) VALUES (?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, session.getClassName());
            stmt.setString(2, session.getStatus().name());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating session failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return session.toBuilder()
                            .id(generatedKeys.getInt(1))
                            .build();
                } else {
                    throw new SQLException("Creating session failed, no ID obtained.");
                }
            }
        }
    }

    public void updateSessionStatus(int sessionId, VotingSession.Status status) throws SQLException {
        String sql = "UPDATE voting_sessions SET status = ?, completed_at = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            
            if (status == VotingSession.Status.COMPLETED) {
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setInt(3, sessionId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating session status failed, no rows affected.");
            }
        }
    }

    public Optional<VotingSession> getCurrentSession() throws SQLException {
        String sql = "SELECT * FROM voting_sessions WHERE status != 'COMPLETED' ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return Optional.of(mapResultSetToSession(rs));
            }
        }
        
        return Optional.empty();
    }

    public Optional<VotingSession> getSessionById(int id) throws SQLException {
        String sql = "SELECT * FROM voting_sessions WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSession(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    public List<VotingSession> getAllSessions() throws SQLException {
        String sql = "SELECT * FROM voting_sessions ORDER BY created_at DESC";
        List<VotingSession> sessions = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        }
        
        return sessions;
    }

    public List<VotingSession> getCompletedSessions() throws SQLException {
        String sql = "SELECT * FROM voting_sessions WHERE status = 'COMPLETED' ORDER BY completed_at DESC";
        List<VotingSession> sessions = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        }
        
        return sessions;
    }

    public void deleteSession(int sessionId) throws SQLException {
        String sql = "DELETE FROM voting_sessions WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
        }
    }

    public boolean hasActiveSession() throws SQLException {
        String sql = "SELECT COUNT(*) FROM voting_sessions WHERE status != 'COMPLETED'";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }

    public void completeSession(int sessionId) throws SQLException {
        updateSessionStatus(sessionId, VotingSession.Status.COMPLETED);
    }

    private VotingSession mapResultSetToSession(ResultSet rs) throws SQLException {
        return VotingSession.builder()
                .id(rs.getInt("id"))
                .className(rs.getString("class_name"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at") != null 
                    ? rs.getTimestamp("created_at").toLocalDateTime() 
                    : LocalDateTime.now())
                .completedAt(rs.getTimestamp("completed_at") != null 
                    ? rs.getTimestamp("completed_at").toLocalDateTime() 
                    : null)
                .build();
    }
}