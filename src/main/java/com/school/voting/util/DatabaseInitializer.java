// ABOUTME: Utility class for initializing database with sample data for testing
// ABOUTME: Creates a pre-populated session with parents and candidates

package com.school.voting.util;

import com.school.voting.dao.ParentDAO;
import com.school.voting.dao.VotingSessionDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.VotingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DatabaseInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    private final VotingSessionDAO sessionDAO = new VotingSessionDAO();
    private final ParentDAO parentDAO = new ParentDAO();
    
    public void initializeSampleData() throws SQLException {
        logger.info("Initializing database with sample data");
        
        // Create session for class 6c
        VotingSession session = VotingSession.builder()
                .className("6c")
                .status(VotingSession.Status.SETUP)
                .build();
        
        session = sessionDAO.createSession(session);
        logger.info("Created session for class 6c with ID: {}", session.getId());
        
        // List of 15 random parent names
        List<String> parentNames = Arrays.asList(
            "Anna Weber",
            "Marcus Müller", 
            "Sophie Schmidt",
            "David Fischer",
            "Julia Becker",
            "Thomas Meyer",
            "Sarah Wagner",
            "Michael Koch",
            "Laura Schulz",
            "Alexander Richter",
            "Emma Hoffmann",
            "Maximilian Klein",
            "Lisa Zimmermann",
            "Sebastian Wolf",
            "Marie Hartmann"
        );
        
        // Add all parents to the session
        for (String name : parentNames) {
            Parent parent = Parent.builder()
                    .name(name)
                    .sessionId(session.getId())
                    .build();
            
            parent = parentDAO.insertParent(parent);
            logger.debug("Added parent: {}", name);
        }
        
        // Randomly select 5 parents to be candidates
        List<Parent> allParents = parentDAO.getParentsBySession(session.getId());
        Random random = new Random();
        
        // Mark first 5 parents as candidates for simplicity
        for (int i = 0; i < 5 && i < allParents.size(); i++) {
            Parent candidate = allParents.get(i);
            parentDAO.markAsCandidate(candidate.getId(), true);
            logger.info("Marked {} as candidate", candidate.getName());
        }
        
        logger.info("Sample data initialization completed - {} parents, 5 candidates", 
                   parentNames.size());
    }
    
    public boolean shouldInitialize() throws SQLException {
        // Check if there's already data in the database
        return sessionDAO.getAllSessions().isEmpty();
    }
}