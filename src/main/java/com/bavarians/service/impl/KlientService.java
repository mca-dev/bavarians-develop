package com.bavarians.service.impl;

import com.bavarians.dto.UserDto;
import com.bavarians.graphql.model.Klient;
import com.bavarians.graphql.repository.KlientRepository;
import com.bavarians.graphql.repository.RoleRepository;
import com.bavarians.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class KlientService implements UserService {
    @Autowired
    private KlientRepository klientRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Klient findByEmail(String email) {
        return klientRepository.findByEmail(email);
    }

    public Iterable<Klient> findAll() {
        return klientRepository.findAll();
    }

    @Override
    public Klient getCurrentKlient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Klient byEmail = klientRepository.findByEmail(userDetails.getUsername());
        return byEmail;
    }

    @Override
    public void save(Klient klient) {
        klient.setHaslo(bCryptPasswordEncoder.encode(klient.getHaslo()));
        klient.setRoles(new HashSet<>(List.of(roleRepository.findByName("ROLE_USER"))));
        klientRepository.save(klient);
    }

    @Override
    public Klient save(UserDto user) {
        Klient klient = new Klient();
        klient.setEmail(user.getUsername());
        klient.setHaslo(bCryptPasswordEncoder.encode(user.getPassword()));
        klient.setRoles(new HashSet<>(List.of(roleRepository.findByName("ROLE_USER"))));
        return klientRepository.save(klient);
    }

}
