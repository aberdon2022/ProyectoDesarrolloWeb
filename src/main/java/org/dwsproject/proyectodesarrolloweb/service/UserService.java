package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public String registerUser (User user) {
        if (userRepository.findByUsername(user.getUsername()) == null) {//Check if the user already exists
            userRepository.save(user);//Save the user
            return "User registered successfully";
        } else {
            return "User already exists";
        }
    }

    public boolean checkPassword (User user, String password) {//Check if the password is correct
        return user.checkPassword(password);
    }
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String addFriend (String username, String friendUsername) {
        User user = userRepository.findByUsername(username);
        User friend = userRepository.findByUsername(friendUsername);

        if (user != null && friend != null) {
            if (!user.getFriends().contains(friend)) {
                if (user.equals(friend)) {
                    return "You cannot add yourself as a friend";
                }
                user.addFriend(friend);
                friend.addFriend(user);
                userRepository.save(user);
                userRepository.save(friend);
                return "Friend added successfully";
            } else {
                return "Friend already exists in the user's friend list";
            }
        } else {
            return "User or friend not found";
        }
    }

    public String deleteFriend(String username, String friendUsername) {
        User user = userRepository.findByUsername(username);
        User friend = userRepository.findByUsername(friendUsername);

        if (user != null && friend != null) {
            if (user.getFriends().contains(friend)) {
                user.deleteFriend(friend); //Delete the friend from the user's friend list
                friend.deleteFriend(user); //Delete the user from the friend's friend list
                userRepository.save(user);
                userRepository.save(friend);
                return "Friend deleted successfully.";
            } else {
                return "Friend not found in the user's friend list.";
            }
        } else {
            return "User or friend not found.";
        }
    }
}

