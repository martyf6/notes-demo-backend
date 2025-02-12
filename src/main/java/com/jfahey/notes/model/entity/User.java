package com.jfahey.notes.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_data")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(length = 60)
    private String password;

    @Column(name = "account_type")
    @Convert(converter = AccountTypeConverter.class)
    private AccountType accountType;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
