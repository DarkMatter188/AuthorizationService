package com.example.userservice.dtos;

import com.example.userservice.models.Token;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String tokenValue;
}
