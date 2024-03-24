package org.dwsproject.proyectodesarrolloweb.service;

import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public boolean checkPassword (User user, String password) {//Check if the password is correct
        return user.checkPassword(password);
    }
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteFriend (String username){
        User friend = findUserByUsername(username);
        friend.deleteFriend(friend);
    }
}

