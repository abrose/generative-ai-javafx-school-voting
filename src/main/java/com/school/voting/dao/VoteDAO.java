// ABOUTME: Data Access Object for Vote entities, handles vote recording and retrieval
// ABOUTME: Ensures vote integrity and provides result calculations

package com.school.voting.dao;

import com.school.voting.model.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteDAO {
    private static final Logger logger = LoggerFactory.getLogger(VoteDAO.class);
    private final DatabaseManager dbManager;

    public VoteDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public Vote recordVote(Vote vote) throws SQLException {
        String sql = "INSERT INTO votes (voter_id, candidate_id, session_id) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, vote.getVoterId());
            stmt.setInt(2, vote.getCandidateId());
            stmt.setInt(3, vote.getSessionId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Recording vote failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Vote(
                        generatedKeys.getInt(1),
                        vote.getVoterId(),
                        vote.getCandidateId(),
                        vote.getSessionId(),
                        vote.getVotedAt()
                    );
                } else {
                    throw new SQLException("Recording vote failed, no ID obtained.");
                }
            }
        }
    }

    public List<Vote> getVotesBySession(int sessionId) throws SQLException {
        String sql = "SELECT * FROM votes WHERE session_id = ? ORDER BY voted_at";
        List<Vote> votes = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    votes.add(mapResultSetToVote(rs));
                }
            }
        }
        
        return votes;
    }

    public int getVoteCount(int candidateId, int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM votes WHERE candidate_id = ? AND session_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidateId);
            stmt.setInt(2, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }

    public Map<Integer, Integer> getVoteCountsBySession(int sessionId) throws SQLException {
        String sql = "SELECT candidate_id, COUNT(*) as vote_count FROM votes " +
                    "WHERE session_id = ? GROUP BY candidate_id ORDER BY vote_count DESC";
        Map<Integer, Integer> voteCounts = new HashMap<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    voteCounts.put(rs.getInt("candidate_id"), rs.getInt("vote_count"));
                }
            }
        }
        
        return voteCounts;
    }

    public boolean hasVoted(int voterId, int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM votes WHERE voter_id = ? AND session_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, voterId);
            stmt.setInt(2, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    public List<Vote> getVotesByCandidateAndSession(int candidateId, int sessionId) throws SQLException {
        String sql = "SELECT * FROM votes WHERE candidate_id = ? AND session_id = ? ORDER BY voted_at";
        List<Vote> votes = new ArrayList<>();
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidateId);
            stmt.setInt(2, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    votes.add(mapResultSetToVote(rs));
                }
            }
        }
        
        return votes;
    }

    public int getTotalVotes(int sessionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM votes WHERE session_id = ?";
        
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

    public void deleteVotesBySession(int sessionId) throws SQLException {
        String sql = "DELETE FROM votes WHERE session_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
        }
    }

    private Vote mapResultSetToVote(ResultSet rs) throws SQLException {
        return new Vote(
            rs.getInt("id"),
            rs.getInt("voter_id"),
            rs.getInt("candidate_id"),
            rs.getInt("session_id"),
            rs.getTimestamp("voted_at") != null 
                ? rs.getTimestamp("voted_at").toLocalDateTime() 
                : LocalDateTime.now()
        );
    }
}