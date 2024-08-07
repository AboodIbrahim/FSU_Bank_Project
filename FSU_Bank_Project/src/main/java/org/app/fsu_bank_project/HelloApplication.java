package org.app.fsu_bank_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.app.fsu_bank_project.server.Server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("FSUGenBank!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws UnknownHostException, SQLException {
        launch();

    }
}