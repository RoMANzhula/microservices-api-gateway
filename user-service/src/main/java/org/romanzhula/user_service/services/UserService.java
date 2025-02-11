package org.romanzhula.user_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.repositories.UserRepository;
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
                .map(user -> new UserResponse())
                .toList()
        ;
    }

}
