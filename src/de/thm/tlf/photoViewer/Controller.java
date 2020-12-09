package de.thm.tlf.photoViewer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller Class for Image-viewer.
 * Holds controls for the GUI elements.
 *
 * @author Tim Lukas Förster
 */
public class Controller extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        GridPane mainPane = new GridPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(mainPane, 300, 275));
        primaryStage.show();
    }
}
