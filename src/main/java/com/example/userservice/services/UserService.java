package com.example.userservice.services;

import com.example.userservice.models.Token;
import com.example.userservice.models.User;

public interface UserService {
    Token login(String email, String password);

    User signup(String name, String email, String password);

    User validateToken(String tokenValue);

    void logout(String tokenValue);
}
