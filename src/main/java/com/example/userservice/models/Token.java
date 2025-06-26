package com.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;

@Data
@Entity(name = "myToken")
public class Token extends BaseModel{
    private String value;
    @ManyToOne
    private User user;
    private Date expiryAt;
}
/*
1              1
Token ------> User
M              1

*/
