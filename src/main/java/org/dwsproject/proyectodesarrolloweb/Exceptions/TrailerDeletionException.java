package org.dwsproject.proyectodesarrolloweb.Exceptions;

public class TrailerDeletionException extends RuntimeException {
    public TrailerDeletionException(String message) {
        super(message);
    }
    
    public TrailerDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
