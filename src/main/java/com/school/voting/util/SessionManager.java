// ABOUTME: Singleton manager for tracking current voting session state
// ABOUTME: Provides centralized access to session data and voting progress

package com.school.voting.util;

import com.school.voting.dao.ParentDAO;
import com.school.voting.dao.VoteDAO;
import com.school.voting.dao.VotingSessionDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.VotingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static SessionManager instance;
    
    private final VotingSessionDAO sessionDAO;
    private final ParentDAO parentDAO;
    private final VoteDAO voteDAO;
    
    private VotingSession currentSession;
    private List<Parent> currentVoters;
    private int currentVoterIndex = 0;
    
    private SessionManager() {
        this.sessionDAO = new VotingSessionDAO();
        this.parentDAO = new ParentDAO();
        this.voteDAO = new VoteDAO();
        loadCurrentSession();
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    private void loadCurrentSession() {
        try {
            Optional<VotingSession> session = sessionDAO.getCurrentSession();
            if (session.isPresent()) {
                this.currentSession = session.get();
                logger.info("Loaded session: {} with status: {}", 
                    currentSession.getId(), currentSession.getStatus());
            }
        } catch (SQLException e) {
            logger.error("Failed to load current session", e);
        }
    }
    
    public VotingSession createNewSession(String className) throws SQLException {
        if (hasActiveSession()) {
            throw new IllegalStateException("Cannot create new session while another is active");
        }
        
        VotingSession newSession = VotingSession.builder()
                .className(className)
                .status(VotingSession.Status.SETUP)
                .build();
        
        currentSession = sessionDAO.createSession(newSession);
        logger.info("Created new voting session for class: {}", className);
        return currentSession;
    }
    
    public void startVoting() throws SQLException {
        if (currentSession == null) {
            throw new IllegalStateException("No active session");
        }
        
        if (!currentSession.canStartVoting()) {
            throw new IllegalStateException("Session is not in SETUP status");
        }
        
        // Verify we have at least 2 candidates
        List<Parent> candidates = parentDAO.getCandidatesBySession(currentSession.getId());
        if (candidates.size() < 2) {
            throw new IllegalStateException("At least 2 candidates required to start voting");
        }
        
        sessionDAO.updateSessionStatus(currentSession.getId(), VotingSession.Status.VOTING);
        currentSession = currentSession.toBuilder()
                .status(VotingSession.Status.VOTING)
                .build();
        
        // Load voters who haven't voted yet
        loadVoters();
        currentVoterIndex = 0;
        
        logger.info("Started voting for session: {}", currentSession.getId());
    }
    
    public void completeSession() throws SQLException {
        if (currentSession == null || !currentSession.isVoting()) {
            throw new IllegalStateException("No active voting session");
        }
        
        sessionDAO.completeSession(currentSession.getId());
        currentSession = currentSession.toBuilder()
                .status(VotingSession.Status.COMPLETED)
                .completedAt(java.time.LocalDateTime.now())
                .build();
        
        logger.info("Completed voting session: {}", currentSession.getId());
    }
    
    public Parent getCurrentVoter() {
        if (currentVoters == null || currentVoters.isEmpty()) {
            return null;
        }
        
        if (currentVoterIndex >= currentVoters.size()) {
            return null;
        }
        
        return currentVoters.get(currentVoterIndex);
    }
    
    public Parent getNextVoter() throws SQLException {
        currentVoterIndex++;
        
        // If we've gone through all voters, reload to get remaining unvoted parents
        if (currentVoterIndex >= currentVoters.size()) {
            loadVoters();
            if (currentVoters.isEmpty()) {
                logger.info("No more voters remaining - voting complete");
                return null; // No more voters
            }
            currentVoterIndex = 0;
        }
        
        return getCurrentVoter();
    }
    
    public void skipCurrentVoter() throws SQLException {
        Parent currentVoter = getCurrentVoter();
        if (currentVoter != null) {
            logger.info("Skipping voter: {} (ID: {})", currentVoter.getName(), currentVoter.getId());
            
            // Mark parent as voted (even though skipped, they won't vote again)
            parentDAO.markAsVoted(currentVoter.getId());
            
            // Move to next voter
            Parent nextVoter = getNextVoter();
            logger.info("Next voter after skipped {}: {}", currentVoter.getName(), 
                       nextVoter != null ? nextVoter.getName() : "None (voting complete)");
        }
    }
    
    public void recordVote(int candidateId) throws SQLException {
        Parent currentVoter = getCurrentVoter();
        if (currentVoter == null) {
            throw new IllegalStateException("No current voter");
        }
        
        logger.info("Recording vote for voter: {} (ID: {})", currentVoter.getName(), currentVoter.getId());
        
        // Mark parent as voted
        parentDAO.markAsVoted(currentVoter.getId());
        
        // Move to next voter
        Parent nextVoter = getNextVoter();
        logger.info("Next voter after {}: {}", currentVoter.getName(), 
                   nextVoter != null ? nextVoter.getName() : "None (voting complete)");
    }
    
    private void loadVoters() throws SQLException {
        if (currentSession == null) {
            throw new IllegalStateException("No active session");
        }
        
        currentVoters = parentDAO.getVotersBySession(currentSession.getId());
        logger.info("Loaded {} remaining voters", currentVoters.size());
    }
    
    public int getTotalParentCount() throws SQLException {
        if (currentSession == null) return 0;
        return parentDAO.getParentCount(currentSession.getId());
    }
    
    public int getVotedCount() throws SQLException {
        if (currentSession == null) return 0;
        return parentDAO.getVotedCount(currentSession.getId());
    }
    
    public int getRemainingVoterCount() throws SQLException {
        return getTotalParentCount() - getVotedCount();
    }
    
    public VotingSession getCurrentSession() {
        return currentSession;
    }
    
    public boolean hasActiveSession() {
        return currentSession != null && currentSession.isActive();
    }
    
    public void clearSession() {
        currentSession = null;
        currentVoters = null;
        currentVoterIndex = 0;
    }
}