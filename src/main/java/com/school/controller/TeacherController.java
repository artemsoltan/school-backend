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
import org.springframework.security.access.AccessDeniedException;
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

    @PostMapping("/subjects/set")
    public ResponseEntity<?> setSubject(@RequestBody String[] subjects, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            teacherService.setSubjects(subjects, jwt);
            return new ResponseEntity<>("Successful!", HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/class/create")
    public ResponseEntity<?> newClass(@RequestBody ClassDTO classDTO, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        System.out.println(classDTO.toString());

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            teacherService.newClass(classDTO, jwtUtil.extractUsername(jwt));
        }

        return new ResponseEntity<>("This class is exists!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeachers(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            List<String> names = teacherService.getAllTeachers(jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(names, HttpStatus.OK);
        }

        return new ResponseEntity<>("You don`t have permission!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/classes/get")
    public ResponseEntity<?> getAllClasses(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            List<String> names = teacherService.getAllClasses(jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(names, HttpStatus.OK);
        }

        return new ResponseEntity<>("You don`t have permission!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/class/delete")
    public ResponseEntity<?> deleteClass(@RequestHeader("Authorization") String jwt, @RequestBody String className) {
        jwt = jwt.substring(7);
        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            try {
                teacherService.deleteClass(jwtUtil.extractUsername(jwt), className);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (AccessDeniedException e) {
                return new ResponseEntity<>("You don`t have permission!", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("You don`t have permission!", HttpStatus.BAD_REQUEST);
    }
}