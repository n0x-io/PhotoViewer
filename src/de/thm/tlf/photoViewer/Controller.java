package de.thm.tlf.photoViewer;

import de.thm.tlf.photoViewer.data.PicturePreview;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Controller Class for Image-viewer.
 * Holds GUI elements as well as the handling of any user input.
 *
 * @author Tim Lukas Förster
 * @version 1.0
 */
public class Controller extends Application {
    ////////////////////////////
    //------ Attributes ------//
    ////////////////////////////

    // CENTER //
    private final ScrollPane currentViewSP = new ScrollPane();
    private final ImageView centerImageView = new ImageView();

    // TOP //
    private final VBox menu = new VBox();
    private final MenuBar menuBar = new MenuBar();
    private final Menu fileMenu = new Menu("File");
    private final Menu aboutMenu = new Menu("About");
    private final MenuItem openFiles = new MenuItem("Open");
    private final MenuItem clearViewer = new Menu("Close all");
    private final MenuItem startSlideShow = new MenuItem("Start Slide Show");
    private final MenuItem exitViewer = new Menu("Exit");
    private final MenuItem showInfo = new Menu("Information");

    // BOTTOM //
    private final VBox bottomPanel = new VBox();

    private final HBox selectionPane = new HBox();
    private final ScrollPane pictureSelector = new ScrollPane();

    private final BorderPane bottomLowerPanel = new BorderPane();
    private final HBox bottomLeft = new HBox();
    private final HBox bottomMid = new HBox();
    private final HBox bottomRight = new HBox();

    private final Button openFilesButton = new Button("Open Pictures");
    private Slider zoomSlider;
    private Slider slideShowSpeedSlider;

    private final Button prevPicBtn = new Button("<-");
    private final Button nextPicBtn = new Button("->");
    private final Button slideShowBtn = new Button("Slide Show");

    private final Button fullScreenBtn = new Button("Fullscreen");

    // Picture handling //
    private PictureHandler picHandler;

    // Etc. //
    private boolean bIsFullScreen = false;
    private boolean bSlideShowActive = false;
    private final DoubleProperty slideShowSpeed = new SimpleDoubleProperty(4);
    private final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    private final KeyCombination keyCrtlQ = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_ANY);
    ////////////////////////////////
    //------ End Attributes ------//
    ////////////////////////////////


    /////////////////////////
    //------ Methods ------//
    /////////////////////////
    /**
     * Entrypoint to program
     * @param args String-Array supplied when starting the application
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Method initializing GUI
     * @param primaryStage Default stage supplied when GUI is created
     */
    @Override
    public void start(Stage primaryStage) {
        picHandler = PictureHandler.getInstance();

        BorderPane root = new BorderPane();
        root.setCenter(createCenter());
        root.setTop(createTop());
        root.setBottom(createBottom());
        Scene mainScene = new Scene(root, 1200, 800);

        // Create all Actions //
        createSliderActions();
        createMenuActions(primaryStage);
        createButtonActions(primaryStage);

        // Keypress Actions //
        mainScene.setOnKeyPressed(event -> {

            if (keyCrtlQ.match(event)) {
                Platform.exit();
            }
            else{
                switch (event.getCode()) {
                    case RIGHT:
                    case A:
                        try {
                            centerImageView.setImage(picHandler.getPrevPicture().getImage());
                        } catch (NoPicturesLoadedException npl) {
                            showNoPicturesLoadedWarning();
                        }
                        break;
                    case LEFT:
                    case D:
                        try {
                            centerImageView.setImage(picHandler.getNextPicture().getImage());
                        } catch (NoPicturesLoadedException npl) {
                            showNoPicturesLoadedWarning();
                        }
                        break;
                }
            }
        });

        primaryStage.setTitle("Photo Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    /**
     * Task used to run the slideshow in separate Threat.
     */
    @SuppressWarnings("rawtypes")
    Task slideShowTask = new Task<Void>(){
        @Override
        @SuppressWarnings("BusyWait")
        protected Void call() throws Exception {
            while(bSlideShowActive) {
                Thread.sleep((long)(slideShowSpeed.get()*1000));
                centerImageView.setImage(picHandler.getNextPicture().getImage());
            }
            return null;
        }
    };

    /**
     * Even wrapper for openFileDialogEvent
     * @param stage the primary stage, used to display the file-open-dialog
     * @return EventHandler for dialog handling
     */
    private EventHandler<ActionEvent> openFileDialogEvent(Stage stage) {
        return e -> openFileDialog(stage);
    }

    /**
     * Event wrapper for clear function
     * @return EventHandle executing the clear function
     */
    private EventHandler<ActionEvent> clearPreviewViewEvent(){
        return e -> clearPreviewView();
    }

    ////////////////////////////////
    //------ Helper-Methods ------//
    ////////////////////////////////
    /**
     * Helper method for handling sliders
     */
    private void createSliderActions() {
        // Zoom Action //
        zoomProperty.addListener(observable -> {
            centerImageView.setFitWidth(zoomProperty.get() * 4);
            centerImageView.setFitHeight(zoomProperty.get() * 3);
        });

        zoomSlider.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            if (oldVal.doubleValue() < newVal.doubleValue()){
                zoomProperty.set(zoomProperty.get() + 10);
            }
            else if (oldVal.doubleValue() > newVal.doubleValue()){
                zoomProperty.set(zoomProperty.get() - 10);
            }
        });

        // SlideShowSpeed Action //
        slideShowSpeed.addListener(observable -> {});
        slideShowSpeedSlider.valueProperty().addListener((observableValue, oldVal, newVal) -> slideShowSpeed.set((double)newVal));
    }

    /**
     * Helper method for handling menus and -entries
     * @param stage the primary stage, used to display the file-open-dialog
     */
    private void createMenuActions(Stage stage) {
        openFiles.setOnAction(openFileDialogEvent(stage));
        exitViewer.setOnAction( e -> Platform.exit());
        clearViewer.setOnAction(clearPreviewViewEvent());
    }

    /**
     * Helper method for handling button interaction
     * @param stage the primary stage, used to display the file-open-dialog and control fullscreen functions
     */
    private void createButtonActions(Stage stage) {
        openFilesButton.setOnAction(openFileDialogEvent(stage));

        prevPicBtn.setOnAction(e -> {
            try{
                centerImageView.setImage(picHandler.getPrevPicture().getImage());
            }
            catch (NoPicturesLoadedException npl){ showNoPicturesLoadedWarning();}
        });

        nextPicBtn.setOnAction(e -> {
            try{
                centerImageView.setImage(picHandler.getNextPicture().getImage());
            }
            catch (NoPicturesLoadedException npl){ showNoPicturesLoadedWarning();}
        });

        fullScreenBtn.setOnAction(e -> {
            stage.setFullScreen(!bIsFullScreen);
            bIsFullScreen ^= true;
        });

        slideShowBtn.setOnAction(new EventHandler<>() {
            Thread th;

            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if (!bSlideShowActive) {
                        // This statement is to catch any errors regarding no images loaded
                        centerImageView.setImage(picHandler.getNextPicture().getImage());
                        bSlideShowActive = true;
                        slideShowBtn.setText("Stop Slide Show");
                        th = new Thread(slideShowTask);
                        th.start();
                    } else {
                        bSlideShowActive = false;
                        slideShowBtn.setText("Slide Show");
                        th.interrupt();
                    }
                } catch (NoPicturesLoadedException npl){
                    showNoPicturesLoadedWarning();
                }
            }
        });
    }

    /**
     * Open file-dialog allowing to select multiple pictures (via filter),
     * adds them to the PictureHandler and updates the picture preview
     * @param stage the primary stage, used to display the file-open-dialog
     */
    private void openFileDialog(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Pictures");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        List<File> selectedPictures = fileChooser.showOpenMultipleDialog(stage);
        if (selectedPictures != null) {
            try {
                picHandler.loadPictures(selectedPictures);
                Controller.this.updatePreviewView();
                centerImageView.setImage(picHandler.getNextPicture().getImage());
            } catch (NoPicturesLoadedException npl) {
                showNoPicturesLoadedWarning();
            }
        }
    }

    /**
     * Method that updates the preview Pane with all PreviewPictures provided by the Picture Handler
     */
    private void updatePreviewView(){
        clearPreviewView();
        selectionPane.setPadding(new Insets(5,5,5,5));
        for(PicturePreview pp: picHandler.getPreviews()){
            ImageView iv = new ImageView(pp.getImage());
            iv.setFitWidth(150);
            iv.setSmooth(true);
            iv.setPreserveRatio(true);
            selectionPane.getChildren().add(iv);
        }
    }

    /**
     * Used to clear the preview and center panel when the viewer is cleared
     */
    private void clearPreviewView(){
        selectionPane.getChildren().clear();
        selectionPane.setPadding(new Insets(5,155,5,5));
        centerImageView.setImage(null);
    }

    /**
     * Method that handles the settings and layout of the top Panel
     * @return Node, set up for displaying on the top of a GridPane
     */
    private Node createTop(){
        SeparatorMenuItem sep = new SeparatorMenuItem();
        // File Menu
        fileMenu.getItems().addAll(openFiles, clearViewer, sep, startSlideShow, exitViewer);
        // About Menu
        aboutMenu.getItems().addAll(showInfo);
        // Menu bar
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
        // Use stack pane and magic to center the image on screen
        StackPane imageHolder = new StackPane(centerImageView);
        GridPane grid = new GridPane();
        currentViewSP.setContent(imageHolder);

        imageHolder.minWidthProperty().bind(Bindings.createDoubleBinding(() ->
                currentViewSP.getViewportBounds().getWidth(), currentViewSP.viewportBoundsProperty()));

        imageHolder.minHeightProperty().bind(Bindings.createDoubleBinding(() ->
                currentViewSP.getViewportBounds().getHeight(), currentViewSP.viewportBoundsProperty()));

        grid.getChildren().add(imageHolder);

        centerImageView.setX(10);
        centerImageView.setY(10);
        centerImageView.setPreserveRatio(true);
        return currentViewSP;
    }

    /**
     * Method that handles the settings and layout of the bottom Panel
     * @return Node, set up for displaying on the bottom of a GridPane
     */
    private Node createBottom() {
        bottomLowerPanel.setPadding(new Insets(5, 5, 5, 5));

        // Left Bottom Part
        zoomSlider = new Slider(0, 100, 25);
        zoomSlider.setShowTickMarks(true);
        Label zoomLabel = new Label("Zoom:");
        bottomLeft.getChildren().addAll(openFilesButton, zoomLabel, zoomSlider);
        //bottomLeft.getChildren().addAll(zoomLabel, zoomSlider);
        bottomLeft.setSpacing(5);
        bottomLowerPanel.setLeft(bottomLeft);

        // Middle Bottom Part
        bottomMid.setAlignment(Pos.CENTER);
        bottomMid.setSpacing(5);
        bottomMid.getChildren().addAll(prevPicBtn, slideShowBtn, nextPicBtn);
        bottomLowerPanel.setCenter(bottomMid);

        // Right Bottom Part
        slideShowSpeedSlider = new Slider(0.5, 15, 4);
        slideShowSpeedSlider.setShowTickMarks(true);
        slideShowSpeedSlider.setShowTickLabels(true);
        Label sssLabel = new Label("Speed:");
        bottomRight.getChildren().addAll(sssLabel,slideShowSpeedSlider, fullScreenBtn);
        bottomLowerPanel.setRight(bottomRight);

        // Bottom upper Part -> Picture preview
        selectionPane.setPadding(new Insets(5,5,155,5));
        selectionPane.autosize();
        selectionPane.setSpacing(5);
        pictureSelector.setContent(selectionPane);

        bottomPanel.getChildren().addAll(pictureSelector, bottomLowerPanel);

        return (bottomPanel);
    }

    /**
     * Displays a warning dialogue informing the user that no pictures has been loaded yet
     * and the executed action is not possible.
     */
    private void showNoPicturesLoadedWarning(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No pictures loaded");
        alert.setContentText("No pictures have been loaded" +
                "\nPlease select pictures via the menu or via the open button to view them.");
        alert.showAndWait().ifPresent(rs -> {
        });
    }
    ////////////////////////////////////
    //------ End Helper-Methods ------//
    ////////////////////////////////////

    /////////////////////////////
    //------ End Methods ------//
    /////////////////////////////
}
