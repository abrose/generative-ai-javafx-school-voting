module com.school.voting {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires kernel;
    requires layout;
    requires java.desktop;

    opens com.school.voting to javafx.fxml;
    opens com.school.voting.controller to javafx.fxml;
    opens com.school.voting.model to javafx.base;

    exports com.school.voting;
    exports com.school.voting.controller;
    exports com.school.voting.model;
    exports com.school.voting.view;
}