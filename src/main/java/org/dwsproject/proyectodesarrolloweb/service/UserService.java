package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private ArrayList<User> usersdb = new ArrayList<>();//Create a list of users
    private ArrayList<User> friendsdb = new ArrayList<>();//Create a list of friends

    public UserService() {//When the service is created, create fake users
        createFakeUsers();
    }

    public void createFakeUsers() {//Create fake users and fake friends
        usersdb.add(new User("user1", "1"));
        friendsdb.add(new User("user2", "2"));
        friendsdb.add(new User("user3", "3"));
        friendsdb.add(new User("user4", "4"));
    }

    public List<User> getFriends(){//Return the list of friends
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
    public void deleteFriends(String username){//Delete a friend with a lambda expression
        friendsdb.removeIf(p -> p.getUsername().equals(username));
    }
}

