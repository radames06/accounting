package com.jd.accounting.controllers;

import com.jd.accounting.model.security.User;
import com.jd.accounting.model.security.UserPrincipal;
import com.jd.accounting.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    Set<User> listUsers() {
        return userService.findAll();
    }

    @PostMapping("/users")
    User newUser(@RequestBody User user) {
        return userService.create(user);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/users")
    User updatePassword(@RequestBody String newPassword, @PathVariable String username) {

        return userService.updatePassword(userService.getCurrentUser(), newPassword);
    }
}
