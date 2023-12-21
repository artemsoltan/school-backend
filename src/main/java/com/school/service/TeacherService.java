package com.school.service;

import com.school.dto.LoginDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.util.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {
    private final PersonRepository personRepository;
    
    @Autowired
    public TeacherService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public boolean isPersonHasSubjects(LoginDTO loginDTO) {
        Person person = personRepository.findByUsername(loginDTO.getUsername()).orElse(null);

        if (person != null) {
            if (!person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
                return true;
            } else return (!person.getSubjects().isEmpty() && person.getRole().equals(RoleEnum.ROLE_TEACHER));
        }

        return false;
    }
}