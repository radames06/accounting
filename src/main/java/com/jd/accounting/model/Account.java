package com.jd.accounting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jd.accounting.model.security.User;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private float initial;

    @JsonIgnore
    @OneToMany( targetEntity = Movement.class, mappedBy = "account")
    private List<Movement> movements = new ArrayList<>();

    @JsonIgnore
    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "username", nullable = false )
    private User user;

}
