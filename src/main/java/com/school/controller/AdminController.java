package com.school.controller;

import com.school.config.jwt.JwtUtil;
import com.school.dto.ClassDTO;
import com.school.dto.ResponseClassDTO;
import com.school.dto.register.StudentDTO;
import com.school.dto.register.TeacherDTO;
import com.school.exception.InvalidCredentialsException;
import com.school.model.Person;
import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import com.school.repository.PersonRepository;
import com.school.repository.SchoolRepository;
import com.school.service.AdminService;
import com.school.service.SaveUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {
    private final JwtUtil jwtUtil;
    private final PersonRepository personRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SaveUserService saveUserService;
    private final AdminService adminService;
    private final SchoolRepository schoolRepository;

    @Autowired
    public AdminController(JwtUtil jwtUtil, PersonRepository personRepository, SchoolClassRepository schoolClassRepository, SaveUserService saveUserService, AdminService adminService, SchoolRepository schoolRepository) {
        this.jwtUtil = jwtUtil;
        this.personRepository = personRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.saveUserService = saveUserService;
        this.adminService = adminService;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        if (isJwtValid(jwt)) {
            List<Person> names = adminService.getAllTeachers(jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(names, HttpStatus.OK);
        }

        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody @Valid TeacherDTO teacherDTO,
                                             @RequestHeader("Authorization") String jwt,
                                             BindingResult bindingResult) {
        jwt = jwt.substring(7);

        if (bindingResult.hasErrors()) {
            throw new InvalidCredentialsException();
        }

        if (isJwtValid(jwt)) {
            saveUserService.saveTeacher(teacherDTO, jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses(@RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        if (isJwtValid(jwt)) {
            List<SchoolClass> classes = adminService.getAllClasses(jwtUtil.extractUsername(jwt));
            if (classes != null) {
                List<ResponseClassDTO> responseClassDTOList = new ArrayList<>();
                for (SchoolClass item : classes) {
                    if (item != null)
                        responseClassDTOList.add(new ResponseClassDTO(item.getId(), item.getName(), item.getTeacher().getName() + " " + item.getTeacher().getSurname()));
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
        if (isJwtValid(jwt)) {
            try {
                adminService.deleteClass(jwtUtil.extractUsername(jwt), className);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (AccessDeniedException e) {
                return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/class")
    public ResponseEntity<?> newClass(@RequestBody ClassDTO classDTO, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        System.out.println(classDTO.toString());

        if (isJwtValid(jwt)) {
            adminService.newClass(classDTO, jwtUtil.extractUsername(jwt));
        }

        return new ResponseEntity<>("This class is exists!", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<?> getClassById(@PathVariable("id") int id, @RequestHeader("Authorization") String jwt){
        jwt = jwt.substring(7);
        SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
        if (schoolClass != null && adminService.doesAdminBelongsToTheSchool(schoolClass.getSchool().getId(), jwtUtil.extractUsername(jwt))) {
            ResponseClassDTO responseClassDTO = new ResponseClassDTO(schoolClass.getId(), schoolClass.getName(), schoolClass.getTeacher().getName() + " " + schoolClass.getTeacher().getSurname());
            return new ResponseEntity<>(responseClassDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/class/{id}/students")
    public ResponseEntity<?> getStudentsByTeacher(@PathVariable("id") int id, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);
        SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
        if (schoolClass != null && adminService.doesAdminBelongsToTheSchool(schoolClass.getSchool().getId(), jwtUtil.extractUsername(jwt))) {
            return new ResponseEntity<>(adminService.getAllStudentsByTeacher(jwtUtil.extractUsername(jwt), id), HttpStatus.OK);
        }
        return new ResponseEntity<>("You don't have permission!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/class/{id}/students")
    public ResponseEntity<?> addStudentsToClass(@PathVariable("id") int id,
                                                @RequestHeader("Authorization") String jwt,
                                                @RequestBody @Valid List<StudentDTO> students,
                                                BindingResult bindingResult) {
        SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);

        if (students.isEmpty()) {
            throw new InvalidCredentialsException();
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidCredentialsException();
        }

        jwt = jwt.substring(7);
        if (schoolClass != null && adminService.doesAdminBelongsToTheSchool(schoolClass.getSchool().getId(), jwtUtil.extractUsername(jwt))) {
            saveUserService.saveStudents(id, students, jwtUtil.extractUsername(jwt));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>("You don't have permission!", HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/class/{id}/student")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") int id, @RequestParam("email") String email, @RequestHeader("Authorization") String jwt) {
        jwt = jwt.substring(7);

        SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
        if (schoolClass != null && adminService.doesAdminBelongsToTheSchool(schoolClass.getSchool().getId(), jwtUtil.extractUsername(jwt))) {
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

    private boolean isJwtValid(String jwt) {
        return jwtUtil.isTokenValid(jwt) && !jwtUtil.extractUsername(jwt).isEmpty();
    }
}