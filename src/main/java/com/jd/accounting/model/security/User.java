package com.jd.accounting.model.security;

import com.jd.accounting.model.Account;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    private int id;
    @Id
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private boolean enabled;
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name="users_roles",
            joinColumns= {@JoinColumn(name="username")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    private List<Role> roles;

    @OneToMany( targetEntity = Account.class, mappedBy = "user")
    private List<Account> accounts = new ArrayList();

//    public int getId() {
//        return id;
//    }
//    public void setId(int id) {
//        this.id = id;
//    }


    public User(String username) {
        this.username = username;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public List<Role> getRoles() {
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}