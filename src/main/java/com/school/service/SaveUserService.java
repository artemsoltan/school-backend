package com.school.service;

import com.school.dto.PersonDTO;
import com.school.model.Person;
import com.school.model.Role;
import com.school.model.School;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.util.RoleEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SaveUserService {
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    public SaveUserService(RoleRepository roleRepository, SchoolRepository schoolRepository, PasswordEncoder passwordEncoder, PersonRepository personRepository) {
        this.roleRepository = roleRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    public void saveTeacher(PersonDTO personDTO) {
        Role role = roleRepository.findByName(RoleEnum.ROLE_TEACHER);
        School school = schoolRepository.findByCode(personDTO.getCode());

        Person person = new Person(personDTO.getName(),
                                    personDTO.getSurname(),
                                    personDTO.getDate(),
                                    personDTO.getUsername(),
                                    passwordEncoder.encode(personDTO.getPassword()),
                                    role,
                                    school
        );

        personRepository.save(person);
    }
}