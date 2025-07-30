// ABOUTME: Data Access Object for Parent entities, handles all database operations
// ABOUTME: Provides CRUD operations and specialized queries for parent management

package com.school.voting.dao;

import com.school.voting.model.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParentDAO {
    private static final Logger logger = LoggerFactory.getLogger(ParentDAO.class);
    private final DatabaseManager dbManager;

    public ParentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Parent insertParent(Parent parent) throws SQLException {
        String sql = "INSERT INTO parents (name, is_candidate, has_voted, session_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, parent.getName());
            stmt.setBoolean(2, parent.isCandidate());
            stmt.setBoolean(3, parent.hasVoted());
            stmt.setInt(4, parent.getSessionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating parent failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return parent.toBuilder()
                            .id(generatedKeys.getInt(1))
                            .build();
                } else {
                    throw new SQLException("Creating parent failed, no ID obtained.");
                }
            }
        }
    }

    public void updateParent(Parent parent) throws SQLException {
        String sql = "UPDATE parents SET name = ?, is_candidate = ?, has_voted = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, parent.getName());
            stmt.setBoolean(2, parent.isCandidate());
            stmt.setBoolean(3, parent.hasVoted());
            stmt.setInt(4, parent.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating parent failed, no rows affected.");
            }
        }
    }

    public Optional<Parent> getParentById(int id) throws SQLException {
        String sql = "SELECT * FROM parents WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToParent(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    public List<Parent> getParentsBySession(int sessionId) throws SQLException {
        String sql = "SELECT * FROM parents WHERE session_id = ? ORDER BY name";
        List<Parent> parents = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parents.add(mapResultSetToParent(rs));
                }
            }
        }
        
        return parents;
    }

    public List<Parent> getCandidatesBySession(int sessionId) throws SQLException {
        String sql = "SELECT * FROM parents WHERE session_id = ? AND is_candidate = 1 ORDER BY name";
        List<Parent> candidates = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    candidates.add(mapResultSetToParent(rs));
                }
            }
        }
        
        return candidates;
    }

    public List<Parent> getVotersBySession(int sessionId) throws SQLException {
        String sql = "SELECT * FROM parents WHERE session_id = ? AND has_voted = 0 ORDER BY name";
        List<Parent> voters = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    voters.add(mapResultSetToParent(rs));
                }
            }
        }
        
        return voters;
    }

    public void markAsCandidate(int parentId, boolean isCandidate) throws SQLException {
        String sql = "UPDATE parents SET is_candidate = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, isCandidate);
            stmt.setInt(2, parentId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Marking parent as candidate failed, no rows affected.");
            }
        }
    }

    public void markAsVoted(int parentId) throws SQLException {
        String sql = "UPDATE parents SET has_voted = 1 WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, parentId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Marking parent as voted failed, no rows affected.");
            }
        }
    }

    public void deleteParent(int parentId) throws SQLException {
        String sql = "DELETE FROM parents WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, parentId);
            stmt.executeUpdate();
        }
    }

    public int getParentCount(int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM parents WHERE session_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    public int getVotedCount(int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM parents WHERE session_id = ? AND has_voted = 1";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    private Parent mapResultSetToParent(ResultSet rs) throws SQLException {
        return Parent.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .isCandidate(rs.getBoolean("is_candidate"))
                .hasVoted(rs.getBoolean("has_voted"))
                .sessionId(rs.getInt("session_id"))
                .createdAt(rs.getTimestamp("created_at") != null 
                    ? rs.getTimestamp("created_at").toLocalDateTime() 
                    : LocalDateTime.now())
                .build();
    }
}