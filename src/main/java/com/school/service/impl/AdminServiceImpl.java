package com.school.service.impl;

import com.school.model.Person;
import org.springframework.stereotype.Service;

@Service
public interface AdminServiceImpl {
    boolean isPersonAdmin(Person person);
}