package com.school.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SchoolService {
    public String generateCode() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }
}