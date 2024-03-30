package org.dwsproject.proyectodesarrolloweb.Exceptions;

public class TrailerUploadException extends Exception {
    public TrailerUploadException(String errorString) {
        super(errorString);
    }
    
    public TrailerUploadException(String errorString, Throwable cause) {
        super(errorString, cause);
    }

    public TrailerUploadException(Throwable cause) {
        super(cause);
    }
    
}
