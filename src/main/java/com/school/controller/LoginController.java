package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.LoginDTO;
import com.school.dto.TeacherRegistrationDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.repository.RoleRepository;
import com.school.repository.SchoolRepository;
import com.school.service.ErrorService;
import com.school.service.LoginService;
import com.school.service.SaveUserService;
import com.school.util.ErrorModel;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public LoginController(PersonRepository personRepository,
                           SchoolRepository schoolRepository,
                           ErrorService errorService,
                           RoleRepository roleRepository,
                           SaveUserService saveUserService,
                           LoginService loginService,
                           JwtUtil jwtUtil) {
        this.personRepository = personRepository;
        this.schoolRepository = schoolRepository;
        this.errorService = errorService;
        this.roleRepository = roleRepository;
        this.saveUserService = saveUserService;
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
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
            if (jwtUtil.isTokenValid(jwt) && jwtUtil.getData(jwt) != null) {
                System.out.println("Data from token: " + jwtUtil.getData(jwt));
                return new ResponseEntity<>(jwtUtil.getData(jwt), HttpStatus.OK);
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}