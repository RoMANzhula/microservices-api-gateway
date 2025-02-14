package org.romanzhula.user_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.requests.UserRequest;
import org.romanzhula.user_service.responses.UserResponse;
import org.romanzhula.user_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<String> addNewUser(
            @RequestBody UserRequest newUser
    ) {
        return ResponseEntity.ok(userService.addNewUser(newUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

}
