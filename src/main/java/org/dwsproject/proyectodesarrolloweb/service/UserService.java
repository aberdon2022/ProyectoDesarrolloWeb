package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
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
        usersdb.add(new User("user2", "2"));
        usersdb.add(new User("user3", "3"));
        usersdb.add(new User("user4", "4"));
    }

    public boolean checkPassword (User user, String password) {//Check if the password is correct
        return user.checkPassword(password);
    }

    public User findUserByUsername(String username) {//Find user by username
        for (User user : usersdb) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void deleteFriend (String username){
        User friend = findUserByUsername(username);
        friend.deleteFriend(friend);
    }
}

