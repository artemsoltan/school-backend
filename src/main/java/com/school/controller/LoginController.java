package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.LoginDTO;
import com.school.dto.TeacherRegistrationDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.service.*;
import com.school.util.ErrorModel;

import com.school.util.RoleEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final PersonRepository personRepository;
    private final SchoolRepository schoolRepository;
    private final ErrorService errorService;
    private final RoleRepository roleRepository;
    private final SaveUserService saveUserService;
    private final LoginService loginService;
    private final JwtUtil jwtUtil;
    private final SchoolService schoolService;
    private final TeacherService teacherService;

    @Autowired
    public LoginController(PersonRepository personRepository,
                           SchoolRepository schoolRepository,
                           ErrorService errorService,
                           RoleRepository roleRepository,
                           SaveUserService saveUserService,
                           LoginService loginService,
                           JwtUtil jwtUtil, SchoolService schoolService, TeacherService teacherService) {
        this.personRepository = personRepository;
        this.schoolRepository = schoolRepository;
        this.errorService = errorService;
        this.roleRepository = roleRepository;
        this.saveUserService = saveUserService;
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
        this.schoolService = schoolService;
        this.teacherService = teacherService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrationPage(@RequestBody TeacherRegistrationDTO teacherRegistrationDTO) {
        ErrorModel error;

        System.out.println(teacherRegistrationDTO.toString());
        if (schoolRepository.findByCode(teacherRegistrationDTO.getCode()) == null) {
            error = errorService.generateError("bad", "Code is incorrect!", "Ваш код неправильний!");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } else if (personRepository.findByUsername(teacherRegistrationDTO.getUsername()).isEmpty()) {
            saveUserService.saveTeacher(teacherRegistrationDTO);

            error = errorService.generateError("ok", "Registration successful!", "Реєстрація успішна!");
            return new ResponseEntity<>(error, HttpStatus.OK);
        }

        error = errorService.generateError("bad" , "Teacher with this username is registered!", "Вчитель з таким іменем вже існує!");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPage(@RequestBody LoginDTO loginDTO) {
        Person person = personRepository.findByUsername(loginDTO.getUsername()).orElse(null);
        if (loginService.accountAvailable(loginDTO) && person != null) {
            System.out.println("Login ok for user: " + loginDTO.getUsername());
            return ResponseEntity.ok().header(
                    HttpHeaders.AUTHORIZATION,
                    jwtUtil.generateToken(person)
            ).body("");
        }

        System.out.println("Login error");
        ErrorModel error = errorService.generateError("bad", "Your password or username is incorrect!", "Ви ввели неапривльний пароль або нікнейм!");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getData")
    public ResponseEntity<?> getDataFromJwt(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        try {
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
                } else {
                    return new ResponseEntity<>("Token is incorrect", HttpStatus.UNAUTHORIZED);
                }
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}