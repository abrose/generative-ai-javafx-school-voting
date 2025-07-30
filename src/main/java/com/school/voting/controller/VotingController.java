// ABOUTME: Controller for the voting interface handling sequential parent voting
// ABOUTME: Manages voter progression, candidate selection, and vote recording

package com.school.voting.controller;

import com.school.voting.dao.ParentDAO;
import com.school.voting.dao.VoteDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.Vote;
import com.school.voting.model.VotingSession;
import com.school.voting.util.SessionManager;
import com.school.voting.view.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class VotingController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(VotingController.class);
    
    @FXML private Label currentVoterLabel;
    @FXML private Label progressLabel;
    @FXML private ProgressBar votingProgressBar;
    @FXML private GridPane candidatesGrid;
    @FXML private Button skipVoterBtn;
    @FXML private Button endVotingBtn;
    @FXML private Label sessionInfoLabel;
    @FXML private Label statusLabel;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ParentDAO parentDAO = new ParentDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    
    private ViewFactory viewFactory;
    private List<Parent> candidates;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::loadVotingData);
    }
    
    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
    
    private void loadVotingData() {
        try {
            // Initialize progress bar to 0
            votingProgressBar.setProgress(0.0);
            
            VotingSession session = sessionManager.getCurrentSession();
            if (session == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No active voting session");
                return;
            }
            
            sessionInfoLabel.setText("Session: " + session.getClassName() + " - " + session.getStatus().getDisplayName());
            
            // Load candidates
            candidates = parentDAO.getCandidatesBySession(session.getId());
            if (candidates.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No candidates found for voting");
                return;
            }
            
            // Setup initial voter
            setupCurrentVoter();
            
        } catch (SQLException e) {
            logger.error("Failed to load voting data", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load voting data: " + e.getMessage());
        }
    }
    
    private void setupCurrentVoter() throws SQLException {
        Parent currentVoter = sessionManager.getCurrentVoter();
        if (currentVoter == null) {
            // No more voters - voting is complete
            completeVoting();
            return;
        }
        
        currentVoterLabel.setText("Current Voter: " + currentVoter.getName());
        
        // Update progress
        int totalParents = sessionManager.getTotalParentCount();
        int votedCount = sessionManager.getVotedCount();
        int currentVoterNumber = votedCount + 1;
        
        progressLabel.setText("Voter " + currentVoterNumber + " of " + totalParents);
        
        // Update progress bar
        double progress = (double) votedCount / totalParents;
        votingProgressBar.setProgress(progress);
        
        // Update status with more detailed info
        int remainingVoters = totalParents - votedCount;
        statusLabel.setText("Select a candidate to continue (" + remainingVoters + " voters remaining)");
        
        logger.info("Current voter: {} (#{} of {})", currentVoter.getName(), currentVoterNumber, totalParents);
        
        // Refresh candidates grid to exclude current voter
        setupCandidatesGrid();
    }
    
    private void setupCandidatesGrid() {
        candidatesGrid.getChildren().clear();
        
        Parent currentVoter = sessionManager.getCurrentVoter();
        
        int columns = 2;
        int row = 0;
        int col = 0;
        
        for (Parent candidate : candidates) {
            // Self-voting is now allowed - all candidates including current voter are shown
            Button candidateBtn = createCandidateButton(candidate);
            candidatesGrid.add(candidateBtn, col, row);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    private Button createCandidateButton(Parent candidate) {
        Button button = new Button(candidate.getName());
        button.getStyleClass().add("candidate-button");
        button.setPrefWidth(300);
        button.setPrefHeight(100);
        button.setFont(Font.font(20));
        button.setAlignment(Pos.CENTER);
        
        button.setOnAction(e -> handleVoteForCandidate(candidate));
        
        return button;
    }
    
    private void handleVoteForCandidate(Parent candidate) {
        Parent currentVoter = sessionManager.getCurrentVoter();
        if (currentVoter == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No current voter");
            return;
        }
        
        // Confirm vote
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Vote");
        confirm.setHeaderText(currentVoter.getName() + " voting for:");
        confirm.setContentText(candidate.getName());
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    recordVoteAndContinue(currentVoter, candidate);
                } catch (SQLException e) {
                    logger.error("Failed to record vote", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to record vote: " + e.getMessage());
                }
            }
        });
    }
    
    private void recordVoteAndContinue(Parent voter, Parent candidate) throws SQLException {
        // Record the vote
        Vote vote = new Vote(voter.getId(), candidate.getId(), sessionManager.getCurrentSession().getId());
        voteDAO.recordVote(vote);
        
        // Update voter status and move to next
        sessionManager.recordVote(candidate.getId());
        
        logger.info("{} voted for {}", voter.getName(), candidate.getName());
        
        // Update UI for next voter or complete voting
        setupCurrentVoter();
    }
    
    @FXML
    private void handleSkipVoter() {
        Parent currentVoter = sessionManager.getCurrentVoter();
        if (currentVoter == null) {
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Skip Voter");
        confirm.setHeaderText("Skip voting for:");
        confirm.setContentText(currentVoter.getName() + "\n\nThis voter will not cast a vote.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sessionManager.skipCurrentVoter();
                    logger.info("Skipped voter: {}", currentVoter.getName());
                    setupCurrentVoter();
                } catch (SQLException e) {
                    logger.error("Failed to skip voter", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to skip voter: " + e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void handleEndVoting() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("End Voting");
        confirm.setHeaderText("End voting session?");
        confirm.setContentText("This will complete the voting and show results.\nThis action cannot be undone.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    completeVoting();
                } catch (SQLException e) {
                    logger.error("Failed to complete voting", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete voting: " + e.getMessage());
                }
            }
        });
    }
    
    private void completeVoting() throws SQLException {
        sessionManager.completeSession();
        logger.info("Voting session completed");
        
        if (viewFactory != null) {
            viewFactory.showResultsView();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}