package com.jd.accounting.controllers;

import com.jd.accounting.model.security.User;
import com.jd.accounting.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


//    @PreAuthorize("hasRole('USER')")
//    @PutMapping("/users")
//    User updatePassword(@RequestBody String newPassword, @PathVariable String username) {
//
//        return userService.updatePassword(userService.getCurrentUser(), newPassword);
//    }
}
