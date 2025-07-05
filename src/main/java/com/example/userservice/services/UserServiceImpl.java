package com.example.userservice.services;

import com.example.userservice.dtos.SendEmailDto;
import com.example.userservice.models.Token;
import com.example.userservice.models.User;
import com.example.userservice.repositories.TokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.common.network.Send;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository,
                           TokenRepository tokenRepository,
                           KafkaTemplate kafkaTemplate,
                           ObjectMapper objectMapper){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public Token login(String email, String password) {
        //check if user exists or not then verify pwd then return token by creating it
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            //throw exception or return null token
            return null;
        }

        //Verify pwd with bcrypt matches method
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            //throw exception or invalid pwd
            return null;
        }
        //Now pwd is correct generate token now
        Token token = new Token();
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        //set expiry time in date format 30 days from today
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        token.setExpiryAt(calendar.getTime());

        return tokenRepository.save(token);
    }

    @Override
    public User signup(String name, String email, String password) {
        //BCryptPassword Encoder
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        //Before Saving user to DB push topic and event to Kafka
        //EmailService will read the event from kafka and send Welcome email to user
        //Serialize obj to Json/String format then sent over NW ObjectMapper from Jackson
        SendEmailDto emailDto = new SendEmailDto();
        emailDto.setSubject("Welcome to Kafka Lessons and Enjoy the show!!");
        emailDto.setBody("Happy to have you Onboard, hope you make most out of it");
        emailDto.setEmail(email);
        try {
            kafkaTemplate.send(
                    "sendEmail",
                    objectMapper.writeValueAsString(emailDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }

    @Override
    public User validateToken(String tokenValue) {
        //Validate if token present in DB and deleted is false
        //Also expiry_time should be greater than current time in JPA Query
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue, false, new Date());

        if(optionalToken.isEmpty()){
            return null;
        }
        return optionalToken.get().getUser();
    }

    @Override
    public void logout(String tokenValue) {
        //First validate token if token is present and valid then mark deleted as true
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue,
                false,
                new Date()
        );

        if(optionalToken.isEmpty()){
            //throw exception
            throw new RuntimeException("Token is not correct could not logout try again !!");
        }

        Token token = optionalToken.get();
        token.setDeleted(true);

        tokenRepository.save(token);
    }
}
