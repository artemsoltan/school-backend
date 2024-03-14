package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ResponseClassDTO;
import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import com.school.repository.PersonRepository;
import com.school.service.SaveUserService;
import com.school.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RestControllerAdvice
public class TeacherController {
    private final JwtUtil jwtUtil;
    private final TeacherService teacherService;
    private final PersonRepository personRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SaveUserService saveUserService;

    public TeacherController(JwtUtil jwtUtil, TeacherService teacherService, PersonRepository personRepository, SchoolClassRepository schoolClassRepository, SaveUserService saveUserService) {
        this.jwtUtil = jwtUtil;
        this.teacherService = teacherService;
        this.personRepository = personRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.saveUserService = saveUserService;
    }

    @PostMapping("/subjects")
    public ResponseEntity<?> setSubject(@RequestBody String[] subjects, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            teacherService.setSubjects(subjects, jwt);
            return new ResponseEntity<>("Successful!", HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty()) {
            List<SchoolClass> aClasses = teacherService.getAllClasses(jwtUtil.extractUsername(jwt));
            if (aClasses != null) {
                List<ResponseClassDTO> responseClassDTOList = new ArrayList<>();
                for (SchoolClass item : aClasses) {
                    if (item != null) {
                        responseClassDTOList.add(new ResponseClassDTO(item.getId(), item.getName(), item.getTeacher().getName() + " " + item.getTeacher().getSurname()));
                    }
                }
                return new ResponseEntity<>(responseClassDTOList, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<?> getClassById(@PathVariable("id") int id, @RequestHeader("Authorization") String jwt){
        jwt = jwt.substring(7);
        if (teacherService.doesTeacherBelongsToTheClass(id, jwtUtil.extractUsername(jwt))) {
            SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
            ResponseClassDTO responseClassDTO = new ResponseClassDTO(schoolClass.getId(), schoolClass.getName(), schoolClass.getTeacher().getName() + " " + schoolClass.getTeacher().getSurname());
            return new ResponseEntity<>(responseClassDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/class/{id}/students")
    public ResponseEntity<?> getStudentsByTeacher(@PathVariable("id") int id, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (teacherService.doesTeacherBelongsToTheClass(id, jwtUtil.extractUsername(jwt))) {
            return new ResponseEntity<>(teacherService.getAllStudentsByTeacher(jwtUtil.extractUsername(jwt), id), HttpStatus.OK);
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }
}