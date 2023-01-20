package com.jd.accounting.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jd.accounting.model.security.User;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private float initial;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private float balance;

    @JsonIgnore
    @OneToMany( targetEntity = Movement.class, mappedBy = "account", cascade = CascadeType.ALL)
    private List<Movement> movements = new ArrayList<>();

    @JsonIgnore
    @ManyToOne()
    @JoinColumn( name = "username", nullable = false )
    private User user;


    public String toString() {
        String result = "Name : " + this.name;
        result += " / Id : " + this.id;
        result += " / Initial : " + this.initial;
        return result;
    }

    public void movement(float amount) {
        this.balance += amount;
    }

    public static Account fromId(Long id) {
        Account account = new Account();
        account.id = id;
        return account;
    }

}
