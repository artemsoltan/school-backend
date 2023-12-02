package com.school.service;

import com.school.dto.LoginDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean accountAvailable(LoginDTO loginDTO) {
        Person person = personRepository.findByUsername(loginDTO.getUsername()).orElse(null);

        return person.getUsername().equals(loginDTO.getUsername()) && passwordEncoder.matches(loginDTO.getPassword(), person.getPassword());
    }
}