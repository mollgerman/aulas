
package com.aulas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aulas.exception.ResourceNotFoundException;
import com.aulas.model.User;
import com.aulas.model.enums.Role;
import com.aulas.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT); // Default role
        }
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }


    public List<User> getAllUsersByRole(Role role) {
        List<User> users = userRepository.findByRole(role);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found with role: " + role);
        }
        return users;
    }
}
