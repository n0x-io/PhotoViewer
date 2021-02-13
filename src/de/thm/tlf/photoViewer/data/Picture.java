package de.thm.tlf.photoViewer.data;

import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Data Class that is used for Image-Handling and -storing
 */
public class Picture {
    private Image image;

    public Picture (String fileRef) {
        try {
            image = new Image(new FileInputStream(fileRef));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Image getImage() {
        return image;
    }
}
