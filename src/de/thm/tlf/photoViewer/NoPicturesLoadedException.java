package de.thm.tlf.photoViewer;

/**
 * Exception for handling errors when no pictures are loaded into the program
 * but any of the actions that involve pictures are performed.
 */
public class NoPicturesLoadedException extends Exception{
    NoPicturesLoadedException(String s){
        super(s);
    }
}