package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.model.Person;
import com.school.repository.PersonRepository;
import com.school.service.TeacherService;
import com.school.util.RoleEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final JwtUtil jwtUtil;
    private final TeacherService teacherService;
    private final PersonRepository personRepository;

    public TeacherController(JwtUtil jwtUtil, TeacherService teacherService, PersonRepository personRepository) {
        this.jwtUtil = jwtUtil;
        this.teacherService = teacherService;
        this.personRepository = personRepository;
    }

    @PostMapping("/setSubjects")
    public ResponseEntity<?> setSubject(@RequestBody String[] subjects, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        try {
            if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
                teacherService.setSubjects(subjects, jwt);
                return new ResponseEntity<>("Successful!", HttpStatus.OK);
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect!", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/newClass")
    public ResponseEntity<?> newClass(@RequestBody ClassDTO classDTO, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        System.out.println(classDTO.toString());
        try {
            if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
                teacherService.newClass(classDTO, jwtUtil.extractUsername(jwt));
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect!", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("This class is exists!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getAllTeachers")
    public ResponseEntity<?> getAllTeachers(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        try {
            if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
                List<String> names = teacherService.getAllTeachers(jwtUtil.extractUsername(jwt));
                return new ResponseEntity<>(names, HttpStatus.OK);
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect!", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("You don`t have permission!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getAllClasses")
    public ResponseEntity<?> getAllClasses(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        try {
            if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
                List<String> names = teacherService.getAllClasses(jwtUtil.extractUsername(jwt));
                return new ResponseEntity<>(names, HttpStatus.OK);
            }
        } catch (NullPointerException | SignatureException e) {
            return new ResponseEntity<>("Token is incorrect!", HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token is expired!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("You don`t have permission!",HttpStatus.BAD_REQUEST);
    }
}