package com.jd.accounting.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Movement {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private float amount;
    private String tiers;
    private Date movementDate;

    @ManyToOne //( cascade = CascadeType.ALL )
    @JoinColumn( name = "id_account", nullable = false )
    //@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    //@JsonIdentityReference(alwaysAsId = true)
    @JsonIgnore
    private Account account;

    @JsonProperty("accountId")
    public void setAccountById(Long accountId) {
        account = Account.fromId(accountId);
    }

    @JsonProperty("accountId")
    public Long getAccountById() {
        return id;
    }

    // TODO : Add subcategory & category

    public String toString() {
        String result = "Account : " + this.account.getName();
        result += " / Amount : " + this.amount;
        return result;
    }

}
