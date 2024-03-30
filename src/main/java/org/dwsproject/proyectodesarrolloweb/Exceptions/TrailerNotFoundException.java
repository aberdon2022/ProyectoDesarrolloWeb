package org.dwsproject.proyectodesarrolloweb.Exceptions;

public class TrailerNotFoundException extends RuntimeException{
    public TrailerNotFoundException(String message) {
        super(message);
    }
    
    public TrailerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
