package com.school.controller;

import com.school.dto.SchoolDTO;
import com.school.repository.SchoolRepository;
import com.school.service.SchoolService;
import com.school.util.ErrorModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
public class GuestController {
    private final SchoolService schoolService;
    private final SchoolRepository schoolRepository;

    public GuestController(SchoolService schoolService, SchoolRepository schoolRepository) {
        this.schoolService = schoolService;
        this.schoolRepository = schoolRepository;
    }

    @PostMapping("/createSchool")
    public ResponseEntity<?> createSchool(@RequestBody SchoolDTO schoolDTO) {
        System.out.println(schoolDTO.toString());
        if (schoolRepository.findByEmail(schoolDTO.getEmail()) == null) {
            schoolService.saveSchool(schoolDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorModel("BAD", "This school is exists!", "Ця школа вже існує!"), HttpStatus.OK);
    }
}