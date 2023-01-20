package com.jd.accounting.controllers;

import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Set;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @RolesAllowed("ROLE_ADMIN")
    ResponseEntity<Set> listUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/auth/register")
    ResponseEntity<User> newUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.create(user));
    }

    @PutMapping("/users/update")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
    ResponseEntity<User> updatePassword(@RequestBody User userUpdated) {

        User user = userService.getCurrentUser();

        if (user.getUsername().equals(userUpdated.getUsername())) {
            return ResponseEntity.ok(userService.updateUser(user, userUpdated));
            } else if ( user.getStringRoles().contains("ROLE_ADMIN")) {
            return ResponseEntity.ok(userService.updateUser(userService.loadUserByUsername(userUpdated.getUsername()), userUpdated));
        } else {
            throw new AuthorizationServiceException(ResourceRepository.getResource("jd.exception.userupdateauthorization", userUpdated.getUsername()));
        }
    }
//
//    @PutMapping("/users/updateuser")
//    @RolesAllowed("ROLE_ADMIN")
//    ResponseEntity<User> tmpUpdatePassword(@RequestBody User userUpdated) {
//
//        return ResponseEntity.ok(userService.updatePassword(userUpdated, userUpdated.getPassword()));
//    }

}
