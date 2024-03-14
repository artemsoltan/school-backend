package com.school.service.impl;

import com.school.model.Person;
import org.springframework.stereotype.Service;

@Service
public interface TeacherServiceImpl {
    boolean isPersonTeacher(Person person);
}