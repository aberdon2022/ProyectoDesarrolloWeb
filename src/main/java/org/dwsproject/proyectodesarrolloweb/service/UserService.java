package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    private ArrayList<User> usersdb = new ArrayList<>();

    public UserService() {
        createFakeUsers();
    }

    public void createFakeUsers() {//Create fake users
        usersdb.add(new User("user1", "1"));
    }

    public User findUserByUsername(String username) {//Find user by username
        for (User user : usersdb) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
