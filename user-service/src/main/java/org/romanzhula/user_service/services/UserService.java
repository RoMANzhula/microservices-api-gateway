package org.romanzhula.user_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.models.User;
import org.romanzhula.user_service.repositories.UserRepository;
import org.romanzhula.user_service.requests.UserRequest;
import org.romanzhula.user_service.responses.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getEmail()
                ))
                .toList()
        ;
    }


    @Transactional
    public String addNewUser(UserRequest newUser) {
        User user = User.builder()
                .username(newUser.getUsername())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .phoneNumber(newUser.getPhoneNumber())
                .build()
        ;

        userRepository.save(user);

        return "User added successfully!";
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getEmail()
                ))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId))
        ;
    }

}
