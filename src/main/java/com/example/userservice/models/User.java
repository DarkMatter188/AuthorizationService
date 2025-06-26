package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.List;

@Data
@Entity(name = "myUser")
public class User extends BaseModel{
    private String name;
    private String email;
    private String password;
    @ManyToMany
    private List<Role> roles;
}
/*
 1            M
User -----> Roles
  M           1
 */
