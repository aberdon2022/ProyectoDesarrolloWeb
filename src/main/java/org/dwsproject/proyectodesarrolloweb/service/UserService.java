package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private ArrayList<User> usersdb = new ArrayList<>();

    public UserService() {
        createFakeUsers();
    }

    public void createFakeUsers() {//Create fake users
        usersdb.add(new User("user1", "1"));
        usersdb.add(new User("user5", "5"));
        usersdb.add(new User("user2", "2"));
        usersdb.add(new User("user6", "6"));
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

