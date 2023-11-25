package com.school.controller;

import com.school.dto.PersonDTO;
import com.school.model.Person;
import com.school.model.Role;
import com.school.model.School;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.service.ErrorService;
import com.school.util.ErrorModel;
import com.school.util.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class LoginController {

    private final PersonRepository personRepository;
    private final SchoolRepository schoolRepository;
    private final ErrorService errorService;
    private final RoleRepository roleRepository;

    @Autowired
    public LoginController(PersonRepository personRepository, SchoolRepository schoolRepository, ErrorService errorService, RoleRepository roleRepository) {
        this.personRepository = personRepository;
        this.schoolRepository = schoolRepository;
        this.errorService = errorService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/show")
    public ResponseEntity<?> showPage(@RequestParam int id) {
        System.out.println("[SERVER: GET Mapping] /show");
        try {
            Person person = personRepository.findById(id).orElseThrow();
            return new ResponseEntity<>(person, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // we register teacher

    @PostMapping("/register")
    public ResponseEntity<?> showPage(@RequestBody PersonDTO personDTO) {
        ErrorModel error;

        System.out.println(personDTO.toString());
        if (schoolRepository.findByCode(personDTO.getCode()) == null) {
            error = errorService.generateError("bad", "Code is incorrect!");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } else if (personRepository.findByUsername(personDTO.getUsername()).isEmpty()) {

            School school = schoolRepository.findByCode(personDTO.getCode());
            Role role = roleRepository.findByName(RoleEnum.ROLE_TEACHER);
            Person teacher = new Person(personDTO.getName(), personDTO.getSurname(), personDTO.getDate(), personDTO.getUsername(), personDTO.getPassword(), role, school);

            personRepository.save(teacher);
            
            error = errorService.generateError("ok", "Registration successful!");
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        error = errorService.generateError("bad" , "Teacher with this username is registered!");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}