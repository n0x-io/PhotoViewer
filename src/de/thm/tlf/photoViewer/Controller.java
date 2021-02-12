package de.thm.tlf.photoViewer;

import de.thm.tlf.photoViewer.data.PicturePreview;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.value.ChangeListener;
///import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Controller Class for Image-viewer.
 * Holds GUI elements
 *
 * @author Tim Lukas Förster
 * @version 0.1
 */

public class Controller extends Application {

    // CENTER //
    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    private final ScrollPane currentViewSP = new ScrollPane();
    private final ImageView centerImageView = new ImageView();

    // LEFT //
    private final VBox leftPane = new VBox();
    private final VBox selectionPane = new VBox();
    private final ScrollPane pictureSelector = new ScrollPane();

    // TOP //
    private final VBox menu = new VBox();
    private final MenuBar menuBar = new MenuBar();
    private final Menu fileMenu = new Menu("File");
    private final Menu aboutMenu = new Menu("About");
    private final MenuItem openFiles = new MenuItem("Open");
    private final MenuItem startSlideShow = new MenuItem("Start Slide Show");
    private final MenuItem exitViewer = new Menu("Exit");
    private final MenuItem showInfo = new Menu("Information");

    // BOTTOM //
    private final BorderPane bottomPanel = new BorderPane();
    private final HBox bottomLeft = new HBox();
    private final HBox bottomMid = new HBox();
    private final HBox bottomRight = new HBox();

    private Slider zoomSlider;
    private final Button openFilesButton = new Button("Open Pictures");

    private final Button prevPicBtn = new Button("<-");
    private final Button nextPicBtn = new Button("->");
    private final Button slideShowBtn = new Button("Slide Show");

    private final Button fullScreenBtn = new Button("Fullscreen");

    // Picture handling //
    private PictureHandler picHandler;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        picHandler = new PictureHandler();

        BorderPane root = new BorderPane();
        root.setLeft(createLeft());
        root.setCenter(createCenter());
        root.setTop(createTop());
        root.setBottom(createBottom());
        Scene mainScene = new Scene(root, 1200, 800);

        //// Keypress Actions ////
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT:
                    try{
                        centerImageView.setImage(picHandler.getPrevPicture().getImage());
                    }
                    catch (NoPicturesLoadedException npl){ npl.printStackTrace();}
                    break;
                case LEFT:
                    try{
                        centerImageView.setImage(picHandler.getNextPicture().getImage());
                    }
                    catch (NoPicturesLoadedException npl){ npl.printStackTrace();}
                    break;
            }
        });
        //// End Keypress Actions ////

        //// Center Zoom Action ////
        zoomProperty.addListener(observable -> {
            centerImageView.setFitWidth(zoomProperty.get());
            centerImageView.setFitHeight(zoomProperty.get());
            //centerImageView.setFitWidth(zoomProperty.get() * 4);
            //centerImageView.setFitHeight(zoomProperty.get() * 3);
        });

        zoomSlider.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (oldVal.doubleValue() < newVal.doubleValue()){
                zoomProperty.set(zoomProperty.get() + 10);
            }
            else if (oldVal.doubleValue() > newVal.doubleValue()){
                zoomProperty.set(zoomProperty.get() - 10);
            }
        });

        /* Currently not used. A method that allow scrolling with the mouse wheel when inside the center
        currentViewSP.addEventFilter(ScrollEvent.ANY, scrollEvent -> {

            if (scrollEvent.getDeltaY() > 0) {
                zoomProperty.set(zoomProperty.get() * 1.1);
            } else if (scrollEvent.getDeltaY() < 0) {
                zoomProperty.set(zoomProperty.get() / 1.1);
            }
        });
        /**/
        //// End Center Zoom Action ////

        //// Menu Actions ////
        openFiles.setOnAction(openFileDialog(primaryStage));
        //// End Menu Actions ////


        //// Button Actions ////
        openFilesButton.setOnAction(openFileDialog(primaryStage));

        prevPicBtn.setOnAction(e -> {
            try{
                centerImageView.setImage(picHandler.getPrevPicture().getImage());
            }
            catch (NoPicturesLoadedException npl){ npl.printStackTrace();}
        });

        nextPicBtn.setOnAction(e -> {
            try{
                centerImageView.setImage(picHandler.getNextPicture().getImage());
            }
            catch (NoPicturesLoadedException npl){ npl.printStackTrace();}
        });

        fullScreenBtn.setOnAction(e -> primaryStage.setFullScreen(true));
        //// End Button Actions ////

        primaryStage.setTitle("Photo Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    /**
     * Open file-dialog allowing to select multiple pictures (via filter),
     * adds them to the PictureHandler and updates the picture preview
     * @param stage the primary stage, used to display the file-open-dialog
     * @return EventHandler for dialog handling
     */
    private EventHandler<ActionEvent> openFileDialog(Stage stage) {
        return e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Pictures");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            List<File> selectedPictures = fileChooser.showOpenMultipleDialog(stage);
            if (selectedPictures != null) {
                try{
                    picHandler.loadPictures(selectedPictures);
                    Controller.this.updatePreviewView();
                    centerImageView.setImage(picHandler.getNextPicture().getImage());
                }
                catch (NoPicturesLoadedException npl){ npl.printStackTrace();}
            }
        };
    }

    /**
     *
     */
    private void updatePreviewView(){
        for(PicturePreview pp: picHandler.getPreviews()){
            ImageView iv = new ImageView(pp.getImage());
            iv.setFitWidth(150);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            selectionPane.getChildren().add(iv);
        }
    }

    /**
     * Method that handles the settings and layout of the Left Panel
     * @return Node, set up for displaying on the left of a GridPane
     */
    private Node createLeft(){
        selectionPane.setPadding(new Insets(5,5,5,5));
        selectionPane.setSpacing(5);
        pictureSelector.setContent(selectionPane);
        leftPane.getChildren().addAll(pictureSelector);
        return leftPane;
    }

    /**
     * Method that handles the settings and layout of the top Panel
     * @return Node, set up for displaying on the top of a GridPane
     */
    private Node createTop(){
        SeparatorMenuItem sep = new SeparatorMenuItem();
        fileMenu.getItems().addAll(openFiles, sep, startSlideShow, exitViewer);
        // About Menu

        aboutMenu.getItems().addAll(showInfo);
        // Menu bare
        menuBar.getMenus().addAll(fileMenu, aboutMenu);
        // Adding Menus to Top Panel
        menu.getChildren().addAll(menuBar);
        return menu;
    }

    /**
     * Method that handles the settings and layout of the center Panel
     * @return Node, set up for displaying on the center of a GridPane
     */
    private Node createCenter(){
        centerImageView.setX(10);
        centerImageView.setY(10);
        centerImageView.setPreserveRatio(true);
        currentViewSP.setContent(centerImageView);
        return currentViewSP;
    }

    /**
     * Method that handles the settings and layout of the bottom Panel
     * @return Node, set up for displaying on the bottom of a GridPane
     */
    private Node createBottom() {
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));

        // Left Bottom Part
        zoomSlider = new Slider(0, 100, 25);
        zoomSlider.setShowTickMarks(true);
        bottomLeft.getChildren().addAll(openFilesButton, zoomSlider);
        bottomPanel.setLeft(bottomLeft);

        // Middle Bottom Part
        bottomMid.setAlignment(Pos.CENTER);
        bottomMid.setSpacing(5);
        bottomMid.getChildren().addAll(prevPicBtn, slideShowBtn, nextPicBtn);
        bottomPanel.setCenter(bottomMid);

        // Right Bottom Part
        bottomRight.getChildren().add(fullScreenBtn);
        bottomPanel.setRight(bottomRight);
        return (bottomPanel);
    }
}
