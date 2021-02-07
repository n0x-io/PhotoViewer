package de.thm.tlf.photoViewer;

public class PictureHandler {

    private PicturePreview[] previews = new PicturePreview[]{
            new PicturePreview("pic/image1.jpg"),
            new PicturePreview("pic/image2.jpg"),
            new PicturePreview("pic/image3.jpg")
    };

    private int currentPictureID = 0;

    public PicturePreview[] getPreviews(){
        return previews;
    }

    public void loadPictures(){

    }

    public Picture nextPicture(){
        return null;
    }

    public Picture prevPicture(){
        return null;
    }

}
