package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.service.TeacherService;
import com.school.util.RoleEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository;
    private final TeacherService teacherService;

    public UserController(JwtUtil jwtUtil, PersonRepository personRepository, TeacherService teacherService) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
        this.teacherService = teacherService;
    }

    @GetMapping("/data")
    public ResponseEntity<?> getDataFromJwt(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            Person person = personRepository.findByUsername(jwtUtil.extractUsername(jwt)).orElse(null);
            if (person != null) {
                Map<String, String> personData = new HashMap<>();

                personData.put("username", person.getUsername());
                personData.put("name", person.getName());
                personData.put("surname", person.getSurname());
                personData.put("date", person.getDate());
                personData.put("role", person.getRole().getName().name());

                if (teacherService.isPersonHasSubjects(person) && person.getRole().getName().equals(RoleEnum.ROLE_TEACHER)) {
                    List<String> subjectNames = new ArrayList<>();
                    List<String> subjectUkraineNames = new ArrayList<>();

                    for (int i = 0; i < person.getSubjects().size(); i++) {
                        subjectNames.add(person.getSubjects().get(i).getName());
                        subjectUkraineNames.add(person.getSubjects().get(i).getUaName());
                    }

                    personData.put("subjectNames", String.join(",", subjectNames));
                    personData.put("subjectUkraineNames", String.join(", ", subjectUkraineNames));
                }

                return new ResponseEntity<>(personData, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}