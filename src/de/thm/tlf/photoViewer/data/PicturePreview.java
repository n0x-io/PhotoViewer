package de.thm.tlf.photoViewer.data;

/**
 * Wrapper of class Picture that's used for handling the preview of pictures
 * -> Used to distinguish between pictures and preview
 */
public class PicturePreview extends Picture{

    public PicturePreview(String fileRef) {
        super(fileRef);
    }
}
