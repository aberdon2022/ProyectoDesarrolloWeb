package org.dwsproject.proyectodesarrolloweb.Service;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.FriendException;
import org.dwsproject.proyectodesarrolloweb.Repositories.FilmRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.dwsproject.proyectodesarrolloweb.Repositories.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final FilmRepository filmRepository;

    private final FriendshipRepository friendshipRepository;

    private final UserSession userSession;


    public UserService(UserRepository userRepository, FilmRepository filmRepository, FriendshipRepository friendshipRepository, UserSession userSession) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.friendshipRepository = friendshipRepository;
        this.userSession = userSession;
    }

    public String registerUser (User user) {
        if (userRepository.findByUsername(user.getUsername()) == null) {//Check if the user already exists
            userRepository.save(user);//Save the user
            return "User registered successfully";
        } else {
            return "User already exists";
        }
    }

    public void saveUser (User user) {
        userRepository.save(user);
    }

    public List<User> getFriends (User user) {
        return user.getFriends().stream()
                .sorted(Comparator.comparing(Friendship::getTimestamp))
                .map(friendship -> friendship.getUser1().equals(user) ? friendship.getUser2() : friendship.getUser1())
                .collect(Collectors.toList());
    }

    public List<Film> getPendingFilms (Long userId) {
        return filmRepository.findByUserIdAndStatus(userId, Film.FilmStatus.PENDING);
    }

    public User getUserProfile (String username) {
        User user = findUserByUsername(username);
        if (user != null) {
            // Fetch pending and completed films directly from the database
            List<Film> pendingFilms = filmRepository.findByUserIdAndStatus(user.getId(), Film.FilmStatus.PENDING);
            List<Film> completedFilms = filmRepository.findByUserIdAndStatus(user.getId(), Film.FilmStatus.COMPLETED);

            user.setPendingFilms(pendingFilms);
            user.setCompletedFilms(completedFilms);
        }
        return user;
    }

    public List<Film> getCompletedFilms (Long userId) {
        return filmRepository.findByUserIdAndStatus(userId, Film.FilmStatus.COMPLETED);
    }

    public boolean checkPassword (User user, String password) {//Check if the password is correct
        User dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser != null) {
            return user.getPassword().equals(password);
        } else {
            return false;
        }
    }
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public String addFriend (String username, String friendUsername) throws FriendException {

        if (username.equals(friendUsername)) {
            return "You can't add yourself as a friend";
        }

        User user = userRepository.findByUsername(username);
        User friend = userRepository.findByUsername(friendUsername);

        if (user != null && friend != null) {
            Friendship existingFriendship = friendshipRepository.findByUser1AndUser2(user, friend);
            if (existingFriendship != null) {
                return "Friend already added";
            }
            // Create a Friendship entity where user1 is the logged-in user and user2 is the friend
            Friendship friendship1 = new Friendship();
            friendship1.setUser1(user);
            friendship1.setUser2(friend);
            friendship1.setTimestamp(LocalDateTime.now());


            // Create a Friendship entity where user1 is the friend and user2 is the logged-in user
            Friendship friendship2 = new Friendship();
            friendship2.setUser1(friend);
            friendship2.setUser2(user);
            friendship2.setTimestamp(LocalDateTime.now());

            friendshipRepository.save(friendship1);
            friendshipRepository.save(friendship2);

            return "Friend added successfully";
            } else {
                return "User or friend not found";
        }

    }

    public String deleteFriend(String username, String friendUsername) throws FriendException {
        User user = userRepository.findByUsername(username);
        User friend = userRepository.findByUsername(friendUsername);

        if (user != null && friend != null) {
            Friendship friendship1 = friendshipRepository.findByUser1AndUser2(user, friend); // Find the friendship where user1 is the logged-in user and user2 is the friend
            Friendship friendship2 = friendshipRepository.findByUser1AndUser2(friend, user); // Find the friendship where user1 is the friend and user2 is the logged-in user
            if (friendship1 != null && friendship2 != null) {
                friendshipRepository.delete(friendship1);
                friendshipRepository.delete(friendship2);
                return "Friend deleted successfully";
            } else {
                throw new FriendException("Friend not found");
            }
        } else {
            throw new FriendException("User or friend not found");
        }
    }

    public User findUserByToken(String token) {
        return userRepository.findByToken(token);
    }
}

