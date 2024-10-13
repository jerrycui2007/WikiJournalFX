package com.wikijournalfx.wikijournalfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    // Constants
    // Dimensions
    final private int SCREEN_WIDTH = 1600;
    final private int SCREEN_HEIGHT = 900;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/wikijournalfx/wikijournalfx/WikiJournal.fxml"));

        // Set up the scene with the FXML content and the screen dimensions
        Scene scene = new Scene(fxmlLoader.load(), SCREEN_WIDTH, SCREEN_HEIGHT);

        // Set the stage title and add the scene
        stage.setTitle("WikiJournal");
        stage.setScene(scene);

        // Show the stage (window)
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}