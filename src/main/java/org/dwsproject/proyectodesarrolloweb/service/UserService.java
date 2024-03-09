package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private ArrayList<User> usersdb = new ArrayList<>();
    private ArrayList<User> friendsdb = new ArrayList<>();

    public UserService() {
        createFakeUsers();
    }

    public void createFakeUsers() {//Create fake users
        usersdb.add(new User("user1", "1"));
        friendsdb.add(new User("user2", "2"));
        friendsdb.add(new User("user3", "3"));
        friendsdb.add(new User("user4", "4"));
    }

    public List<User> getFriends(){
        return this.friendsdb;
    }

    public User findUserByUsername(String username) {//Find user by username
        for (User user : usersdb) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public void deleteFriends(String username){
        friendsdb.removeIf(p -> p.getUsername().equals(username));
    }
}

