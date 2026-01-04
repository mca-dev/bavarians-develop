package com.bavarians.service;


import com.bavarians.graphql.model.Klient;
import com.bavarians.dto.UserDto;
import org.springframework.security.core.Authentication;

public interface UserService {
    Klient getCurrentKlient();

    void save(Klient user);

    Klient findByEmail(String username);

    Klient save(UserDto user);
}