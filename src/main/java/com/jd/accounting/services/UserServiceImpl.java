package com.jd.accounting.services;

import com.jd.accounting.exceptions.UserAlreadyExistsException;
import com.jd.accounting.exceptions.UserNotFoundException;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResourceRepository resourceRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ResourceRepository resourceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public User getCurrentUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loadUserByUsername(principal.getUsername());
        return user;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(resourceRepository.getResource("jd.exception.usernotfound", username)));
    }

    @Override
    public Set<User> findAll() {
        Set<User> userSet = new HashSet<>();
        userRepository.findAll().iterator().forEachRemaining(userSet::add);
        return userSet;
    }

    @Override
    public User create(User user) {
        try {
            userRepository.findByUsername(user.getUsername());
            throw new UserAlreadyExistsException(resourceRepository.getResource("jd.exception.useralreadyexists", user.getUsername()));
        } catch(UserNotFoundException e) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        }
    }
//
//    @Override
//    public User updatePassword(User user, String newPassword) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//
//    }

    // TODO : Doublon
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User NOT Found"));
//        return user;
//    }
}
