package com.example.userservice.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity(name = "myRole")
public class Role extends  BaseModel{
    private String value;
}
