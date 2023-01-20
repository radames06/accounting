package com.jd.accounting.services;

import com.jd.accounting.exceptions.FileReaderException;
import com.jd.accounting.exceptions.UserAlreadyExistsException;
import com.jd.accounting.exceptions.UserNotFoundException;
import com.jd.accounting.model.Account;
import com.jd.accounting.model.Category;
import com.jd.accounting.model.Subcategory;
import com.jd.accounting.model.security.Role;
import com.jd.accounting.model.security.User;
import com.jd.accounting.repositories.ResourceRepository;
import com.jd.accounting.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("classpath:categories.csv")
    Resource resourceFile;

    @Override
    public User getCurrentUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = loadUserByUsername(principal.getUsername());
        return user;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(ResourceRepository.getResource("jd.exception.usernotfound", username)));
    }

    @Override
    public Set<User> findAll() {
        Set<User> userSet = new HashSet<>();
        userRepository.findAll().iterator().forEachRemaining(userSet::add);
        return userSet;
    }

    @Override
    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(ResourceRepository.getResource("jd.exception.useralreadyexists", user.getUsername()));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCategories(readCategoryFile(user));
            List<Role> roles = new ArrayList<>();
            roles.add(new Role("ROLE_USER")); // TODO : Bug - crée un nouveau role dans la DB à chaque utilisateur
            user.setRoles(roles);

            return userRepository.save(user);
        }
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }


    public List<Category> readCategoryFile(User user) {
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        try {
            File file = resourceFile.getFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values[0].equals("C")) {
                    category = new Category();
                    category.setUser(user);
                    category.setName(values[1]);
                    categories.add(category);
                } else if (values[0].equals("S")) {
                    Subcategory subcategory = new Subcategory();
                    subcategory.setCategory(category);
                    subcategory.setName(values[1]);
                    category.getSubcategories().add(subcategory);
                } else {
                    throw new FileReaderException(ResourceRepository.getResource("jd.exception.filereaderexception", line));
                }
            }
        } catch (Exception ex) {
            throw new FileReaderException(ex.getMessage());
        }
        return categories;
    }

    @Override
    public User updateUser(User oldUser, User newUser) {

        if (!newUser.getRoles().isEmpty()) {
            oldUser.setRoles(newUser.getRoles());
        }
        if (newUser.getPassword() != null) {
            oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getProvider() != null) {
            oldUser.setProvider(newUser.getProvider());
        }
        // Update des categories est interdit (il faudrait créer dynamiquement les catégories)

        return userRepository.save(oldUser);
    }
}
