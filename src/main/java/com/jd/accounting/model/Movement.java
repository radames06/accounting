package com.jd.accounting.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Movement {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private float amount;

    @ManyToOne( cascade = CascadeType.ALL )
    @JoinColumn( name = "id_account", nullable = false )
    private Account account;

}
