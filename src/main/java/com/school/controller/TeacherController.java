package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.dto.ResponseClassDTO;
import com.school.dto.StudentDTO;
import com.school.exception.StudentInvalidCredentialsException;
import com.school.model.SchoolClass;
import com.school.model.Person;
import com.school.repository.ClassesRepository;
import com.school.repository.PersonRepository;
import com.school.service.SaveUserService;
import com.school.service.TeacherService;
import com.school.util.ErrorModel;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@Validated
public class TeacherController {
    private final JwtUtil jwtUtil;
    private final TeacherService teacherService;
    private final PersonRepository personRepository;
    private final ClassesRepository classesRepository;
    private final SaveUserService saveUserService;

    public TeacherController(JwtUtil jwtUtil, TeacherService teacherService, PersonRepository personRepository, ClassesRepository classesRepository, SaveUserService saveUserService) {
        this.jwtUtil = jwtUtil;
        this.teacherService = teacherService;
        this.personRepository = personRepository;
        this.classesRepository = classesRepository;
        this.saveUserService = saveUserService;
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
            SchoolClass schoolClass = classesRepository.findById(id).orElse(null);
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

    @PostMapping("/class/{id}/students")
    public ResponseEntity<?> addStudentsToClass(@PathVariable("id") int id,
                                                @RequestHeader("Authorization") String jwt,
                                                @RequestBody @Valid List<StudentDTO> students,
                                                BindingResult bindingResult) {
            if (students.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (bindingResult.hasErrors()) {
                throw new StudentInvalidCredentialsException();
            }

            jwt = jwt.substring(7);
            if (teacherService.doesTeacherBelongsToTheClass(id, jwtUtil.extractUsername(jwt))) {
                saveUserService.saveStudents(id, students, jwtUtil.extractUsername(jwt));
                return new ResponseEntity<>(HttpStatus.OK);
            }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/class/{id}/student")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") int id, @RequestParam("email") String email, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (teacherService.doesTeacherBelongsToTheClass(id, jwtUtil.extractUsername(jwt))) {
            System.out.println(email);
            Person person = personRepository.findByEmail(email).orElse(null);
            if (person != null) {
                personRepository.delete(person);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Student don't found!", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({StudentInvalidCredentialsException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorModel> handleException(Exception e) {
        ErrorModel errorModel = new ErrorModel("Bad request", "Invalid credentials or data", "Неправильні облікові дані або дані!");
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
}