// ABOUTME: Factory class for creating and managing JavaFX views
// ABOUTME: Centralizes view creation and scene management

package com.school.voting.view;

import com.school.voting.controller.AdminController;
import com.school.voting.controller.ResultsController;
import com.school.voting.controller.VotingController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ViewFactory {
    private static final Logger logger = LoggerFactory.getLogger(ViewFactory.class);
    
    private final Stage primaryStage;
    private Scene currentScene;
    
    public ViewFactory(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void showAdminView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin.fxml"));
            Parent root = loader.load();
            
            AdminController controller = loader.getController();
            controller.setViewFactory(this);
            
            showScene(root, "School Voting System - Admin");
        } catch (IOException e) {
            logger.error("Failed to load admin view", e);
            throw new RuntimeException("Failed to load admin view", e);
        }
    }
    
    public void showVotingView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/voting.fxml"));
            Parent root = loader.load();
            
            VotingController controller = loader.getController();
            controller.setViewFactory(this);
            
            showScene(root, "School Voting System - Voting");
        } catch (IOException e) {
            logger.error("Failed to load voting view", e);
            throw new RuntimeException("Failed to load voting view", e);
        }
    }
    
    public void showResultsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/results.fxml"));
            Parent root = loader.load();
            
            ResultsController controller = loader.getController();
            controller.setViewFactory(this);
            
            showScene(root, "School Voting System - Results");
        } catch (IOException e) {
            logger.error("Failed to load results view", e);
            throw new RuntimeException("Failed to load results view", e);
        }
    }
    
    private void showScene(Parent root, String title) {
        if (currentScene == null) {
            currentScene = new Scene(root);
            currentScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(currentScene);
        } else {
            currentScene.setRoot(root);
        }
        
        primaryStage.setTitle(title);
        
        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}