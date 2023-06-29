package ahu.bsc.sbnjplag;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;


public class SBNJPlagTool extends Application {

    private Label directoryLabel;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Parent root = loader.load();


        // Access the controller instance
        SBNJPlagController controller = loader.getController();
        controller.setStage(stage);

        // Set the stage and scene
        Scene scene = new Scene(root, 1000, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("SBNJPlag");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

