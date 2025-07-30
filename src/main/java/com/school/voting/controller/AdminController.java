// ABOUTME: Controller for the admin interface handling parent management and session setup
// ABOUTME: Manages parent addition, candidate selection, and voting session initialization

package com.school.voting.controller;

import com.school.voting.dao.ParentDAO;
import com.school.voting.model.Parent;
import com.school.voting.model.VotingSession;
import com.school.voting.util.SessionManager;
import com.school.voting.view.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @FXML private TextField classNameField;
    @FXML private Button createSessionBtn;
    @FXML private Label sessionInfoLabel;
    
    @FXML private TextField parentNameField;
    @FXML private Button addParentBtn;
    @FXML private VBox parentsListContainer;
    @FXML private Label parentCountLabel;
    
    @FXML private VBox candidatesListContainer;
    @FXML private Label candidateCountLabel;
    
    @FXML private Button startVotingBtn;
    @FXML private Label statusLabel;
    
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ParentDAO parentDAO = new ParentDAO();
    private final Map<Integer, CheckBox> candidateCheckBoxes = new HashMap<>();
    
    private ViewFactory viewFactory;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateUI();
        checkExistingSession();
    }
    
    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
    
    private void checkExistingSession() {
        if (sessionManager.hasActiveSession()) {
            VotingSession session = sessionManager.getCurrentSession();
            classNameField.setText(session.getClassName());
            classNameField.setDisable(true);
            createSessionBtn.setDisable(true);
            
            if (session.getStatus() == VotingSession.Status.SETUP) {
                loadParents();
            }
        }
    }
    
    @FXML
    private void handleCreateSession() {
        String className = classNameField.getText().trim();
        if (className.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a class name");
            return;
        }
        
        try {
            sessionManager.createNewSession(className);
            classNameField.setDisable(true);
            createSessionBtn.setDisable(true);
            updateUI();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Session created for class: " + className);
        } catch (Exception e) {
            logger.error("Failed to create session", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create session: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddParent() {
        String parentName = parentNameField.getText().trim();
        if (parentName.isEmpty()) {
            return;
        }
        
        if (!sessionManager.hasActiveSession()) {
            showAlert(Alert.AlertType.WARNING, "No Session", "Please create a session first");
            return;
        }
        
        try {
            Parent parent = Parent.builder()
                    .name(parentName)
                    .sessionId(sessionManager.getCurrentSession().getId())
                    .build();
            
            parent = parentDAO.insertParent(parent);
            parentNameField.clear();
            loadParents();
            
            Platform.runLater(() -> parentNameField.requestFocus());
        } catch (Exception e) {
            logger.error("Failed to add parent", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add parent: " + e.getMessage());
        }
    }
    
    private void loadParents() {
        if (!sessionManager.hasActiveSession()) {
            return;
        }
        
        try {
            List<Parent> parents = parentDAO.getParentsBySession(sessionManager.getCurrentSession().getId());
            
            // Update parents list
            parentsListContainer.getChildren().clear();
            candidatesListContainer.getChildren().clear();
            candidateCheckBoxes.clear();
            
            for (Parent parent : parents) {
                // Add to parents list
                addParentToList(parent);
                
                // Add to candidates list
                addCandidateCheckbox(parent);
            }
            
            updateCounts();
            updateStartButton();
            
        } catch (SQLException e) {
            logger.error("Failed to load parents", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load parents: " + e.getMessage());
        }
    }
    
    private void addParentToList(Parent parent) {
        HBox parentBox = new HBox(10);
        parentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        parentBox.setPadding(new Insets(5));
        parentBox.getStyleClass().add("parent-item");
        
        Label nameLabel = new Label(parent.getName());
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("delete-button");
        deleteBtn.setOnAction(e -> handleDeleteParent(parent));
        
        parentBox.getChildren().addAll(nameLabel, deleteBtn);
        parentsListContainer.getChildren().add(parentBox);
    }
    
    private void addCandidateCheckbox(Parent parent) {
        CheckBox checkBox = new CheckBox(parent.getName());
        checkBox.setSelected(parent.isCandidate());
        checkBox.setPadding(new Insets(5));
        checkBox.setOnAction(e -> handleCandidateToggle(parent, checkBox.isSelected()));
        
        candidateCheckBoxes.put(parent.getId(), checkBox);
        candidatesListContainer.getChildren().add(checkBox);
    }
    
    private void handleDeleteParent(Parent parent) {
        if (sessionManager.getCurrentSession().getStatus() != VotingSession.Status.SETUP) {
            showAlert(Alert.AlertType.WARNING, "Cannot Delete", "Cannot delete parents after voting has started");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete parent: " + parent.getName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    parentDAO.deleteParent(parent.getId());
                    loadParents();
                } catch (SQLException e) {
                    logger.error("Failed to delete parent", e);
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete parent");
                }
            }
        });
    }
    
    private void handleCandidateToggle(Parent parent, boolean isCandidate) {
        try {
            parentDAO.markAsCandidate(parent.getId(), isCandidate);
            updateCounts();
            updateStartButton();
        } catch (SQLException e) {
            logger.error("Failed to update candidate status", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update candidate status");
        }
    }
    
    @FXML
    private void handleStartVoting() {
        try {
            sessionManager.startVoting();
            
            if (viewFactory != null) {
                viewFactory.showVotingView();
            }
        } catch (Exception e) {
            logger.error("Failed to start voting", e);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to start voting: " + e.getMessage());
        }
    }
    
    private void updateUI() {
        boolean hasSession = sessionManager.hasActiveSession();
        addParentBtn.setDisable(!hasSession);
        parentNameField.setDisable(!hasSession);
        
        if (hasSession) {
            VotingSession session = sessionManager.getCurrentSession();
            sessionInfoLabel.setText("Session: " + session.getClassName() + " - " + session.getStatus().getDisplayName());
        } else {
            sessionInfoLabel.setText("No active session");
        }
    }
    
    private void updateCounts() {
        int totalParents = parentsListContainer.getChildren().size();
        parentCountLabel.setText("Total Parents: " + totalParents);
        
        long candidateCount = candidateCheckBoxes.values().stream()
                .filter(CheckBox::isSelected)
                .count();
        candidateCountLabel.setText("Selected Candidates: " + candidateCount);
    }
    
    private void updateStartButton() {
        long candidateCount = candidateCheckBoxes.values().stream()
                .filter(CheckBox::isSelected)
                .count();
        
        boolean canStart = sessionManager.hasActiveSession() && 
                          sessionManager.getCurrentSession().canStartVoting() &&
                          candidateCount >= 2;
        
        startVotingBtn.setDisable(!canStart);
        
        if (candidateCount < 2) {
            statusLabel.setText("Select at least 2 candidates to start voting");
        } else {
            statusLabel.setText("Ready to start voting with " + candidateCount + " candidates");
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