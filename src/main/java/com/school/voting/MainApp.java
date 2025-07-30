// ABOUTME: Main application entry point for the school voting system
// ABOUTME: Initializes JavaFX application and manages primary stage

package com.school.voting;

import com.school.voting.dao.DatabaseManager;
import com.school.voting.util.DatabaseInitializer;
import com.school.voting.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting School Voting System");
        
        try {
            // Initialize database
            DatabaseManager.getInstance();
            
            // Initialize sample data if database is empty
            DatabaseInitializer initializer = new DatabaseInitializer();
            if (initializer.shouldInitialize()) {
                initializer.initializeSampleData();
            }
            
            // Set up view factory and show admin view
            ViewFactory viewFactory = new ViewFactory(primaryStage);
            viewFactory.showAdminView();
            
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw new RuntimeException("Failed to start application", e);
        }
    }
    
    @Override
    public void stop() {
        logger.info("Shutting down School Voting System");
        DatabaseManager.getInstance().closeConnection();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}