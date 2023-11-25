package com.school.controller;

import com.school.dto.PersonDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.service.ErrorService;
import com.school.service.SaveUserService;
import com.school.util.ErrorModel;
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
    private final SaveUserService saveUserService;

    @Autowired
    public LoginController(PersonRepository personRepository, SchoolRepository schoolRepository, ErrorService errorService, RoleRepository roleRepository, SaveUserService saveUserService) {
        this.personRepository = personRepository;
        this.schoolRepository = schoolRepository;
        this.errorService = errorService;
        this.roleRepository = roleRepository;
        this.saveUserService = saveUserService;
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
            saveUserService.saveTeacher(personDTO);

            error = errorService.generateError("ok", "Registration successful!");
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        error = errorService.generateError("bad" , "Teacher with this username is registered!");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}