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
    //------ Attributes ------//
    private final ArrayList<Picture> pictures = new ArrayList<>();
    private final ArrayList<PicturePreview> previews = new ArrayList<>();

    private int currentPictureID = -1;

    private static final PictureHandler INSTANCE = new PictureHandler();
    //------ End Attributes ------//


    //------ Constructors ------//
    private PictureHandler() {}

    public static PictureHandler getInstance() {return INSTANCE;}
    //------ End Constructors ------//


    //------ Methods ------//
    public ArrayList<PicturePreview> getPreviews(){
        return previews;
    }

    public void loadPictures(List<File> pictureFiles){
        for(File picFile : pictureFiles){
            pictures.add(new Picture(picFile.getPath()));
            previews.add(new PicturePreview(picFile.getPath()));
        }
    }

    public Picture getNextPicture() throws NoPicturesLoadedException {
        if(pictures.size() <= 0){
            throw new NoPicturesLoadedException("No pictures have been loaded");
        }
        else {
            currentPictureID = (currentPictureID + 1) % pictures.size();
            return pictures.get(currentPictureID);
        }
    }

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
