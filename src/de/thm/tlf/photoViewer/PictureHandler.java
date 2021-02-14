package de.thm.tlf.photoViewer;

import de.thm.tlf.photoViewer.data.Picture;
import de.thm.tlf.photoViewer.data.PicturePreview;

import java.io.File;
import java.util.*;

/**
 * Class for handling interaction with Images
 * Uses custom Image-wrappers "Picture" and "PicturePreview"
 * Implemented using singleton pattern to avoid multiple instances
 */
public final class PictureHandler {
    ////////////////////////////
    //------ Attributes ------//
    ////////////////////////////
    private final ArrayList<Picture> pictures = new ArrayList<>();
    private final ArrayList<PicturePreview> previews = new ArrayList<>();

    private int currentPictureID = -1;

    private static final PictureHandler INSTANCE = new PictureHandler();

    //////////////////////////////
    //------ Constructors ------//
    //////////////////////////////

    /**
     * Prevent creation of new instances
     */
    private PictureHandler() {}

    /**
     * Singleton-Pattern method to acquire the only instance
     * @return PictureHandler instance
     */
    public static PictureHandler getInstance() {return INSTANCE;}

    /////////////////////////
    //------ Methods ------//
    /////////////////////////

    /**
     * @return Arraylist of the PicturePreview
     */
    public ArrayList<PicturePreview> getPreviews(){
        return previews;
    }

    /**
     * Method to load a list of Files into Picture objects and store them locally
     * @param pictureFiles List of files that will be converted to Pictures
     */
    public void loadPictures(List<File> pictureFiles){
        pictures.clear();
        previews.clear();
        for(File picFile : pictureFiles){
            pictures.add(new Picture(picFile.getPath()));
            previews.add(new PicturePreview(picFile.getPath()));
        }
    }

    /**
     * Determines the next picture that should be displayed
     * @return Picture object that should be displayed next from the ArrayList pictures
     * @throws NoPicturesLoadedException Exception for when no pictures are loaded yet but tried to access them
     */
    public Picture getNextPicture() throws NoPicturesLoadedException {
        if(pictures.size() <= 0){
            throw new NoPicturesLoadedException("No pictures have been loaded");
        }
        else {
            currentPictureID = (currentPictureID + 1) % pictures.size();
            return pictures.get(currentPictureID);
        }
    }

    /**
     * Determines the previous picture that should be displayed
     * @return Picture object that should be displayed next from the ArrayList pictures
     * @throws NoPicturesLoadedException Exception for when no pictures are loaded yet but tried to access them
     */
    public Picture getPrevPicture() throws NoPicturesLoadedException{
        if(pictures.size() <= 0){
            throw new NoPicturesLoadedException("No pictures have been loaded");
        }
        else {
            currentPictureID = (currentPictureID + pictures.size() - 1) % pictures.size();
            return pictures.get(currentPictureID);
        }
    }
    //------ End Methods ------//
}

