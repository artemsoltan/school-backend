package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.dto.ResponseClassDTO;
import com.school.model.Classes;
import com.school.repository.ClassesRepository;
import com.school.repository.PersonRepository;
import com.school.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final JwtUtil jwtUtil;
    private final TeacherService teacherService;
    private final PersonRepository personRepository;
    private final ClassesRepository classesRepository;

    public TeacherController(JwtUtil jwtUtil, TeacherService teacherService, PersonRepository personRepository, ClassesRepository classesRepository) {
        this.jwtUtil = jwtUtil;
        this.teacherService = teacherService;
        this.personRepository = personRepository;
        this.classesRepository = classesRepository;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllTeachers(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            List<String> names = teacherService.getAllTeachers(jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(names, HttpStatus.OK);
        }

        return new ResponseEntity<>("You don't have permission!",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/classes/get")
    public ResponseEntity<?> getAllClasses(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            List<Classes> classes = teacherService.getAllClasses(jwtUtil.extractUsername(jwt));
            List<ResponseClassDTO> responseClassDTOList = new ArrayList<>();
            for (Classes item : classes) {
                responseClassDTOList.add(new ResponseClassDTO(item.getId(), item.getName(), item.getTeacher().getName() + " " + item.getTeacher().getSurname()));
            }
            return new ResponseEntity<>(responseClassDTOList, HttpStatus.OK);
        }

        return new ResponseEntity<>("You don't have permission!",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/class/delete")
    public ResponseEntity<?> deleteClass(@RequestHeader("Authorization") String jwt, @RequestParam("className") String className) {
        jwt = jwt.substring(7);
        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            try {
                teacherService.deleteClass(jwtUtil.extractUsername(jwt), className);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (AccessDeniedException e) {
                return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/class/create")
    public ResponseEntity<?> newClass(@RequestBody ClassDTO classDTO, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            teacherService.newClass(classDTO, jwtUtil.extractUsername(jwt));
        }

        return new ResponseEntity<>("This class is exists!", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<?> getClassById(@PathVariable("id") int id, @RequestHeader("Authorization") String jwt){
        jwt = jwt.substring(7);
        if (teacherService.doesTeacherBelongsToTheClass(id, jwtUtil.extractUsername(jwt))) {
            Classes classes = classesRepository.findById(id).orElse(null);
            ResponseClassDTO responseClassDTO = new ResponseClassDTO(classes.getId(), classes.getName(), classes.getTeacher().getName() + " " + classes.getTeacher().getSurname());
            return new ResponseEntity<>(responseClassDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }
}