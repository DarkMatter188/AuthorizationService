package com.example.userservice.controllers;

import com.example.userservice.dtos.*;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        Token token = userService.login(requestDto.getEmail(), requestDto.getPassword());
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setTokenValue(token.getValue());

        return responseDto;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignUpRequestDto requestDto){
        User user = userService.signup(requestDto.getName(), requestDto.getEmail(), requestDto.getPassword());

        //Convert User to UserDto then return
        return UserDto.from(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogOutRequestDto requestDto){
        userService.logout(requestDto.getTokenValue());
        return new ResponseEntity<Void>(
                HttpStatus.OK
        );
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<UserDto> validateToken(@PathVariable String token){
        User user = userService.validateToken(token);
        ResponseEntity<UserDto> responseEntity = null;
        if(user == null){
            responseEntity = new ResponseEntity<>(
                    null,
                    HttpStatus.UNAUTHORIZED
            );
        }
        else{
            responseEntity = new ResponseEntity<>(
                    UserDto.from(user),
                    HttpStatus.OK
            );
        }
        return responseEntity;
    }

}
