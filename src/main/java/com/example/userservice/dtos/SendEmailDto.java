package com.example.userservice.dtos;

import lombok.Data;

@Data
public class SendEmailDto {
    private String email;
    private String subject;
    private String body;
}
