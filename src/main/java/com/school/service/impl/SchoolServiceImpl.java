package com.school.service.impl;

import com.school.model.Person;
import com.school.model.SchoolClass;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SchoolServiceImpl {
    List<Person> getAllStudentsByTeacher(String username, int classId);
    List<SchoolClass> getAllClasses(String username);
}