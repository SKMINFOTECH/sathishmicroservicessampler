package me.sathish.accountholdercommand.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "account_holders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "First Name cannot be empty")
    private String firstName;
    @Column(nullable = false)
    @NotEmpty(message = "Last Name cannot be empty")
    private String lastName;
    @Column(nullable = false)
    @NotEmpty(message = "Account Holder type cannot be empty")
    private String type;
    @Column(nullable = false, unique=true)
    @NotEmpty(message = "Account Holder ssn cannot be empty")
    private String ssn;
    @Column(nullable = false, unique=true)
    @Temporal(TemporalType.TIMESTAMP)
    @NotEmpty(message = "Account Holder Date of Birth cannot be empty")
    private String dob;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AccountHolder accountHolder = (AccountHolder) o;
        return id != null && Objects.equals(id, accountHolder.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
