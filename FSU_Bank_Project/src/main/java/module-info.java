module org.app.fsu_bank_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jakarta.mail;



    opens org.app.fsu_bank_project to javafx.fxml;
    exports org.app.fsu_bank_project;
    exports org.app.fsu_bank_project.server;
    opens org.app.fsu_bank_project.server to javafx.fxml;
}