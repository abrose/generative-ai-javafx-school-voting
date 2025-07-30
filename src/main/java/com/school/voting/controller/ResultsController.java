// ABOUTME: Controller for the results view displaying voting outcomes and statistics
// ABOUTME: Shows winner, deputy, complete results table, and voting statistics

package com.school.voting.controller;

import com.school.voting.dao.ParentDAO;
import com.school.voting.dao.VoteDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.VotingSession;
import com.school.voting.util.PdfExportService;
import com.school.voting.util.SessionManager;
import com.school.voting.view.ViewFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ResultsController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ResultsController.class);
    
    @FXML private Label sessionInfoLabel;
    @FXML private Label winnerNameLabel;
    @FXML private Label winnerVotesLabel;
    @FXML private Label deputyNameLabel;
    @FXML private Label deputyVotesLabel;
    @FXML private TableView<ResultRow> resultsTable;
    @FXML private TableColumn<ResultRow, String> candidateColumn;
    @FXML private TableColumn<ResultRow, String> votesColumn;
    @FXML private TableColumn<ResultRow, String> percentageColumn;
    @FXML private Label totalVotesLabel;
    @FXML private Label totalParentsLabel;
    @FXML private Label turnoutLabel;
    @FXML private Button newSessionBtn;
    @FXML private Button exportResultsBtn;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ParentDAO parentDAO = new ParentDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final PdfExportService pdfExportService = new PdfExportService();
    
    private ViewFactory viewFactory;
    private DecimalFormat percentFormat = new DecimalFormat("#.#");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadResults();
    }
    
    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
    
    private void setupTableColumns() {
        candidateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().candidateName));
        votesColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().votes)));
        percentageColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().percentage + "%"));
        
        // Center align the votes and percentage columns
        votesColumn.setStyle("-fx-alignment: CENTER;");
        percentageColumn.setStyle("-fx-alignment: CENTER;");
    }
    
    private void loadResults() {
        try {
            VotingSession session = sessionManager.getCurrentSession();
            if (session == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No voting session found");
                return;
            }
            
            sessionInfoLabel.setText("Session: " + session.getClassName() + " - " + session.getStatus().getDisplayName());
            
            // Get candidates and their vote counts
            List<Parent> candidates = parentDAO.getCandidatesBySession(session.getId());
            Map<Integer, Integer> voteCounts = voteDAO.getVoteCountsBySession(session.getId());
            
            // Create sorted results
            List<ResultRow> results = candidates.stream()
                    .map(candidate -> {
                        int votes = voteCounts.getOrDefault(candidate.getId(), 0);
                        return new ResultRow(candidate.getName(), votes);
                    })
                    .sorted((a, b) -> Integer.compare(b.votes, a.votes)) // Sort by votes descending
                    .collect(Collectors.toList());
            
            // Calculate percentages
            int totalVotes = voteCounts.values().stream().mapToInt(Integer::intValue).sum();
            for (ResultRow result : results) {
                if (totalVotes > 0) {
                    double percentage = (double) result.votes / totalVotes * 100;
                    result.percentage = percentFormat.format(percentage);
                } else {
                    result.percentage = "0";
                }
            }
            
            // Display winner and deputy
            if (!results.isEmpty()) {
                ResultRow winner = results.get(0);
                winnerNameLabel.setText(winner.candidateName);
                winnerVotesLabel.setText(winner.votes + " votes (" + winner.percentage + "%)");
                
                // Check for ties in first place
                boolean hasWinnerTie = results.size() > 1 && results.get(1).votes == winner.votes;
                if (hasWinnerTie) {
                    winnerVotesLabel.setText(winner.votes + " votes (" + winner.percentage + "%) - TIE");
                }
                
                if (results.size() > 1) {
                    ResultRow deputy = results.get(1);
                    deputyNameLabel.setText(deputy.candidateName);
                    deputyVotesLabel.setText(deputy.votes + " votes (" + deputy.percentage + "%)");
                    
                    // Check for ties in second place
                    boolean hasDeputyTie = results.size() > 2 && results.get(2).votes == deputy.votes;
                    if (hasDeputyTie) {
                        deputyVotesLabel.setText(deputy.votes + " votes (" + deputy.percentage + "%) - TIE");
                    }
                } else {
                    deputyNameLabel.setText("No deputy");
                    deputyVotesLabel.setText("");
                }
                
                logger.info("Results: Winner: {} ({} votes), Deputy: {} ({} votes)", 
                           winner.candidateName, winner.votes,
                           results.size() > 1 ? results.get(1).candidateName : "None",
                           results.size() > 1 ? results.get(1).votes : 0);
            } else {
                winnerNameLabel.setText("No candidates");
                winnerVotesLabel.setText("");
                deputyNameLabel.setText("No deputy");
                deputyVotesLabel.setText("");
            }
            
            // Populate results table
            ObservableList<ResultRow> tableData = FXCollections.observableArrayList(results);
            resultsTable.setItems(tableData);
            
            // Display voting statistics
            int totalParents = sessionManager.getTotalParentCount();
            totalVotesLabel.setText(String.valueOf(totalVotes));
            totalParentsLabel.setText(String.valueOf(totalParents));
            
            if (totalParents > 0) {
                double turnoutPercentage = (double) totalVotes / totalParents * 100;
                turnoutLabel.setText(percentFormat.format(turnoutPercentage) + "%");
            } else {
                turnoutLabel.setText("0%");
            }
            
            logger.info("Results loaded: {} candidates, {} total votes, {} parents", 
                       candidates.size(), totalVotes, totalParents);
            
        } catch (SQLException e) {
            logger.error("Failed to load results", e);
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load results: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleNewSession() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("New Session");
        confirm.setHeaderText("Start a new voting session?");
        confirm.setContentText("This will clear the current session and return to the admin panel.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sessionManager.clearSession();
                    if (viewFactory != null) {
                        viewFactory.showAdminView();
                    }
                } catch (Exception e) {
                    logger.error("Failed to start new session", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to start new session: " + e.getMessage());
                }
            }
        });
    }
    
    @FXML
    private void handleExportResults() {
        VotingSession session = sessionManager.getCurrentSession();
        if (session == null) {
            showAlert(Alert.AlertType.ERROR, "Export Error", "No voting session found to export.");
            return;
        }
        
        try {
            // Show progress
            exportResultsBtn.setDisable(true);
            exportResultsBtn.setText("Exporting...");
            
            // Export to PDF
            java.io.File pdfFile = pdfExportService.exportResults(session);
            
            // Show success message with file location
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Export Successful");
            successAlert.setHeaderText("Results exported successfully!");
            successAlert.setContentText("PDF saved as: " + pdfFile.getName() + 
                                       "\nLocation: " + pdfFile.getAbsolutePath());
            
            // Add button to open file location
            successAlert.showAndWait();
            
            // Try to open the file or its directory
            if (java.awt.Desktop.isDesktopSupported()) {
                try {
                    java.io.File parentDir = pdfFile.getParentFile();
                    if (parentDir != null) {
                        java.awt.Desktop.getDesktop().open(parentDir);
                    } else {
                        // If no parent directory, open current working directory
                        java.awt.Desktop.getDesktop().open(new java.io.File("."));
                    }
                } catch (Exception e) {
                    logger.warn("Could not open file location", e);
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to export results to PDF", e);
            showAlert(Alert.AlertType.ERROR, "Export Error", 
                     "Failed to export results: " + e.getMessage());
        } finally {
            // Restore button
            exportResultsBtn.setDisable(false);
            exportResultsBtn.setText("Export Results");
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Inner class for table data
    public static class ResultRow {
        public final String candidateName;
        public final int votes;
        public String percentage;
        
        public ResultRow(String candidateName, int votes) {
            this.candidateName = candidateName;
            this.votes = votes;
            this.percentage = "0";
        }
    }
}