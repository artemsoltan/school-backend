package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.LoginDTO;
import com.school.dto.register.AdminDTO;
import com.school.exception.InvalidCredentialsException;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.repository.SchoolRepository;
import com.school.service.*;
import com.school.util.ErrorModel;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class LoginController {

    private final PersonRepository personRepository;
    private final SchoolRepository schoolRepository;
    private final ErrorService errorService;
    private final SaveUserService saveUserService;
    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(PersonRepository personRepository,
                           SchoolRepository schoolRepository,
                           ErrorService errorService,
                           SaveUserService saveUserService,
                           LoginService loginService,
                           JwtUtil jwtUtil) {
        this.personRepository = personRepository;
        this.schoolRepository = schoolRepository;
        this.errorService = errorService;
        this.saveUserService = saveUserService;
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrationPage(@RequestBody @Valid AdminDTO adminDTO, BindingResult bindingResult) {
        ErrorModel error;

        if (bindingResult.hasErrors()) {
            throw new InvalidCredentialsException();
        }

        if (schoolRepository.findByCode(adminDTO.getCode()) == null) {
            error = errorService.generateError("bad", "Code is incorrect!", "Ваш код неправильний!");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } else if (personRepository.findByUsername(adminDTO.getUsername()).isEmpty()) {
            saveUserService.saveAdmin(adminDTO);

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
            return ResponseEntity.ok().header(
                    HttpHeaders.AUTHORIZATION,
                    jwtUtil.generateToken(person)
            ).body("");
        }

        System.out.println("Login error");
        ErrorModel error = errorService.generateError("bad", "Your password or username is incorrect!", "Ви ввели неапривльний пароль або нікнейм!");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}