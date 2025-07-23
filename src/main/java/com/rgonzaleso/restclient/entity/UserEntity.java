package com.rgonzaleso.restclient.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy=IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
}
