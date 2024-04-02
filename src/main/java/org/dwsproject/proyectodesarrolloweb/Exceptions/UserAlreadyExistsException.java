package org.dwsproject.proyectodesarrolloweb.Exceptions;

public class UserAlreadyExistsException extends Throwable {
    public UserAlreadyExistsException(String usernameAlreadyExists) {
        super(usernameAlreadyExists);
    }
}
