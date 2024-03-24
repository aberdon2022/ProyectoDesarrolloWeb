package org.dwsproject.proyectodesarrolloweb;
import org.dwsproject.proyectodesarrolloweb.Classes.User;
import org.dwsproject.proyectodesarrolloweb.Repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class LoadDatabase {
    private final UserRepository userRepository;

    public LoadDatabase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            List<User> allUsers = userRepository.findAll();
            Map<String, List<User>> usersGrouped = allUsers.stream()
                    .collect(Collectors.groupingBy(User::getUsername));

            for (List<User> users : usersGrouped.values()) {
                if (users.size() > 1) {
                    for (int i = 1; i < users.size(); i++) {
                        userRepository.delete(users.get(i));
                    }
                }
            }
            User user1 = userRepository.findByUsername("user1");
            if (user1 == null) {
                userRepository.save(new User("user1", "1"));
            }
            User user2 = userRepository.findByUsername("user2");
            if (user2 == null) {
                userRepository.save(new User("user2", "2"));
            }
        };
    }
}
