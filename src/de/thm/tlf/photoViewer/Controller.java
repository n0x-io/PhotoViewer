package de.thm.tlf.photoViewer;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Controller Class for Image-viewer.
 * Holds controls for the GUI elements.
 *
 * @author Tim Lukas Förster
 */
public class Controller extends Application {

    private PictureHandler picHandler = new PictureHandler();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        /* ##############################
         * ####      MAIN PANE       ####
         * #############################*/
        BorderPane mainPane = new BorderPane();
        // #### END MAIN PANE ####


        /* ##############################
         * ####     BOTTOM PANEL     ####
         * #############################*/
        BorderPane bottom = new BorderPane();
        bottom.setPadding(new Insets(5,5,5,5));

        // ZoomSlider -> Bottom Left
        HBox bottomLeft = new HBox();
        Slider zoomSlider = new Slider(0, 100, 15);
        zoomSlider.setShowTickMarks(true);
        // Open Button
        Button openFilesButton = new Button("Open Pictures");

        bottomLeft.getChildren().addAll(openFilesButton, zoomSlider);
        bottom.setLeft(bottomLeft);

        HBox bottomMid = new HBox();
        bottomMid.setAlignment(Pos.CENTER);
        bottomMid.setSpacing(5);
        Button prevPicBtn = new Button("<-");
        Button nextPicBtn = new Button("->");
        Button diashowBtn = new Button("Diashow");
        bottomMid.getChildren().addAll(prevPicBtn, diashowBtn, nextPicBtn);
        bottom.setCenter(bottomMid);

        HBox bottomRight = new HBox();
        Button fullScreenBtn = new Button("Fullscreen");
        bottomRight.getChildren().add(fullScreenBtn);
        bottom.setRight(bottomRight);

        //Button Actions
        prevPicBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                picHandler.nextPicture();
            }
        });
        mainPane.setBottom(bottom);
        // #### END BOTTOM PANEL ####


        /* ##############################
         * ####      TOP PANEL       ####
         * #############################*/
        VBox menu = new VBox();
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem openFiles = new MenuItem("Open");
        MenuItem startDiashow = new MenuItem("Start Diashow");
        MenuItem exitViewer = new Menu("Exit");
        SeparatorMenuItem sep = new SeparatorMenuItem();
        fileMenu.getItems().addAll(openFiles, sep, startDiashow, exitViewer);
        // About Menu
        Menu aboutMenu = new Menu("About");
        MenuItem showInfo = new Menu("Information");
        aboutMenu.getItems().addAll(showInfo);
        // Menu bare
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, aboutMenu);
        // Adding Menus to Top Panel
        menu.getChildren().addAll(menuBar);
        mainPane.setTop(menu);
        // #### END TOP PANEL ####


        /* ##############################
         * ####     CENTER PANEL     ####
         * #############################*/
        Picture testPic = new Picture("pic/image3.jpg");
        ScrollPane currentViewSP = new ScrollPane();
        Image image = testPic.getImage();
        //Creating the image view
        ImageView imageView = new ImageView();
        //Setting image to the image view
        imageView.setImage(image);
        //Setting the image view parameters
        imageView.setX(10);
        imageView.setY(10);
        imageView.setPreserveRatio(true);
        currentViewSP.setContent(imageView);
        mainPane.setCenter(currentViewSP);
        // #### END CENTER PANEL ####


        /* ##############################
         * ####      LEFT PANEL      ####
         * #############################*/
        VBox leftPane = new VBox();
        VBox selectionPane = new VBox();
        selectionPane.setPadding(new Insets(5,5,5,5));
        selectionPane.setSpacing(5);

        // Add all previews  to the
        for(PicturePreview pp: picHandler.getPreviews()){
            ImageView iv = new ImageView(pp.getImage());
            iv.setFitWidth(150);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            selectionPane.getChildren().add(iv);
        }
        ScrollPane pictureSelector = new ScrollPane();
        pictureSelector.setContent(selectionPane);

        leftPane.getChildren().addAll(pictureSelector);
        mainPane.setLeft(leftPane);
        // #### END LEFT PANEL ####

        primaryStage.setTitle("Photo Viewer");
        primaryStage.setScene(new Scene(mainPane, 1200, 600));
        primaryStage.show();
    }

}
