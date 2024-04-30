package org.dwsproject.proyectodesarrolloweb.Service;

import org.dwsproject.proyectodesarrolloweb.Classes.Film;
import org.dwsproject.proyectodesarrolloweb.Classes.Friendship;
import org.dwsproject.proyectodesarrolloweb.Classes.Role;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Exceptions.FriendException;
import org.dwsproject.proyectodesarrolloweb.Repositories.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;

    public UserService(UserRepository userRepository, FilmRepository filmRepository, FriendshipRepository friendshipRepository, UserSession userSession, PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       PostRepository postRepository, UserDetailsServiceImpl userDetailsService) {
        this.userRepository = userRepository;
        this.filmRepository = filmRepository;
        this.friendshipRepository = friendshipRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.postRepository = postRepository;
    }

    public void setUserPendingFilms (User user) {
        List<Film> films = getPendingFilms(user.getId());
        user.setPendingFilms(films);
    }

    public void setUserCompletedFilms (User user) {
        List<Film> films = getCompletedFilms(user.getId());
        user.setCompletedFilms(films);
    }

    public User registerUser (String username, String password) {//Register a new user (
        User user = new User(username, password);
        if (userRepository.findByUsername(user.getUsername()) == null) {//Check if the user already exists
            Role role = roleRepository.findByName("USER");
            if (role == null) {
                role = new Role();
                role.setName("USER");
                roleRepository.save(role);
            }
            user.getRoles().add(role);
            saveUser(user);//Save the user
            return user;
        } else {
            throw new RuntimeException("User already exists");
        }
    }

    public void saveUser (User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> getFriends (User user) {
        return user.getFriends().stream()
                .sorted(Comparator.comparing(Friendship::getTimestamp)) // Sort the friends by the date they were added
                .map(friendship -> friendship.getUser1().equals(user) ? friendship.getUser2() : friendship.getUser1()) // Get the friend that is not the logged-in user
                .collect(Collectors.toList()); // Convert the stream to a list
    }

    public List<Film> getPendingFilms (Long userId) {
        return filmRepository.findByUserIdAndStatus(userId, Film.FilmStatus.PENDING);
    }

    public List<Film> getCompletedFilms (Long userId) {
        return filmRepository.findByUserIdAndStatus(userId, Film.FilmStatus.COMPLETED);
    }

    public boolean checkPassword (User user, String password) {//Check if the password is correct
        User dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser != null) {
            return passwordEncoder.matches(password, dbUser.getPassword());
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

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        postRepository.deleteAll(user.getPosts());
        filmRepository.deleteAll(user.getPendingFilms());
        filmRepository.deleteAll(user.getCompletedFilms());
        friendshipRepository.deleteAll(user.getFriends());

        userRepository.delete(user);
    }
    public boolean isAdmin(User user){
        List<Role> roles = user.getRoles();
        for(Role r: roles){
            if(r.getName().equals("ADMIN")) {
                return true;
            }
        }
        return false;
    }
}

