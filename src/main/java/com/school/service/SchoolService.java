package com.school.service;

import com.school.dto.SchoolDTO;
import com.school.model.School;
import com.school.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SchoolService {
    private final SchoolRepository schoolRepository;

    @Autowired
    public SchoolService(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    private String generateCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    public void saveSchool(SchoolDTO schoolDTO) {
        School school = new School(generateCode(), schoolDTO.getName(), schoolDTO.getShortname(), schoolDTO.getCity(), schoolDTO.getEmail());
        schoolRepository.save(school);
    }
}